package com.example.springcachepoc.components;

import static com.example.springcachepoc.utils.Utils.TEST_SIZE;

import java.util.logging.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

import com.example.springcachepoc.repository.Person;
import com.example.springcachepoc.repository.PersonRepository;
import com.example.springcachepoc.service.AsyncService;
import com.example.springcachepoc.utils.Utils;
import java.util.concurrent.TimeUnit;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class Ehcache implements CommandLineRunner {
  @NonNull
  private PersonRepository repository;

  @NonNull
  private AsyncService asyncService;

//  @NonNull
//  private CacheManager manager;

  public CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);

  @Override
  public void run(String... args) throws Exception {
    Cache<Long, Person> writeBehindCache = cacheManager.createCache("writeBehindCache",
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Person.class, ResourcePoolsBuilder.heap(10))
            .withLoaderWriter(new CacheLoaderWriter<Long, Person>() {
              @Override
              public Person load(Long key) throws Exception {
                Person person = repository.findById(key).get();
                return person;
              }

              @Override
              public void write(Long key, Person value) throws Exception {
//                System.out.println("Current Thread: " + Thread.currentThread().getName());
                repository.save(value);
              }

              @Override
              public void delete(Long key) throws Exception {
                repository.delete(repository.findById(key).get());
              }
            }) // <1>
            .withService(WriteBehindConfigurationBuilder // <2>
                .newBatchedWriteBehindConfiguration(30, TimeUnit.SECONDS, 50)// <3>
                .queueSize(50)// <4>
                .concurrencyLevel(5) // <5>
                .enableCoalescing()) // <6>
            .build());

    Logger log = Logger.getLogger("################################## EhCache");
    long startTime = System.nanoTime();
    for (long i = 1; i < TEST_SIZE; i++) {
      Person person = Utils.createRandomPerson((int)i);
//      System.out.println("Current Thread: " + Thread.currentThread().getName());
//      System.out.println("Created new person: " + person);
      writeBehindCache.put(i,person);
    }

    while(repository.count() != TEST_SIZE-1){
      //
    }
    long endTime = System.nanoTime();
    log.info("Saving all persons to DB took: " + TimeUnit.NANOSECONDS.toMillis(endTime - startTime));

    try {
      Thread.sleep(60000,0);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }

//    for (long i = 1; i < 50; i++) {
//      System.out.println(writeBehindCache.get(i));
//    }

//    Cache<Integer, Person> personCache =
//        cacheManager.getCache("personCache", Integer.class, Person.class);
//
//    createPersons();
//    displayPersons();
//
//    cacheManager.removeCache("personCache");

//    cacheManager.close();
  }

  public void createPersons() {
    Cache<Integer, Person> personCache =
        cacheManager.getCache("personCache", Integer.class, Person.class);

    for (int i = 1; i < 50; i++) {
      Person person = Utils.createRandomPerson(i);
      System.out.println("Created new person: " + person);
      personCache.put(i,person);
    }
  }

  public void displayPersons() {
    Cache<Integer, Person> personCache =
        cacheManager.getCache("personCache", Integer.class, Person.class);

    for (int i = 1; i < 50; i++) {
      System.out.println(personCache.get(i));
    }
  }

  public Ehcache(@NonNull final PersonRepository repository, @NonNull AsyncService asyncService) {
//    this.manager = CacheManager.create();
    if (repository == null) {
      throw new NullPointerException("repository is marked non-null but is null");
    } else {
      this.repository = repository;
    }
    if (asyncService == null) {
      throw new NullPointerException("asyncService is marked non-null but is null");
    } else {
      this.asyncService = asyncService;
    }
  }
}
