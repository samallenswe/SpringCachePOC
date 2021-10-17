package com.example.springcachepoc.components;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import com.example.springcachepoc.repository.Person;
import com.example.springcachepoc.repository.PersonRepository;
import com.example.springcachepoc.service.AsyncService;
import com.example.springcachepoc.utils.Utils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class Coherence implements CommandLineRunner {
  @NonNull
  private PersonRepository repository;

  @NonNull
  private AsyncService asyncService;

  private NamedCache personCache = CacheFactory.getCache("VirtualCache");


  @Override
  public void run(String... args) throws Exception {
//    asyncService.asyncSaveCoherence();
//    createPersons();
  }

  public void createPersons() {
    for (int i = 1; i < 200; i++) {
      Person person = Utils.createRandomPerson(i);
      System.out.println("Created new person: " + person);
      personCache.put(i,person);
      try {
        Thread.sleep(200,0);
      } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }


  public Coherence(@NonNull final PersonRepository repository, @NonNull AsyncService asyncService) {
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