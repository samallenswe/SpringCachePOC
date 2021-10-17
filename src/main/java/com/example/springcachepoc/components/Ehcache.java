package com.example.springcachepoc.components;

import com.example.springcachepoc.utils.PersonLoaderWriter;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Order(3)
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
    Cache<Long, String> personCache = cacheManager.createCache("personCache",
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))
            .withLoaderWriter(new PersonLoaderWriter<Integer, Person>())
            .add(WriteBehindConfigurationBuilder
                .newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 3)
                .queueSize(3)
                .concurrencyLevel(1)
                .enableCoalescing())
            .build());

//    Cache<Integer, Person> personCache =
//        cacheManager.getCache("personCache", Integer.class, Person.class);

    createPersons();
    displayPersons();

    cacheManager.removeCache("personCache");
    cacheManager.close();
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
