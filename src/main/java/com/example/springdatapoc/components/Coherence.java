package com.example.springdatapoc.components;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import static com.example.springdatapoc.utils.Utils.TEST_SIZE;

import com.example.springdatapoc.repository.Person;
import com.example.springdatapoc.repository.PersonRepository;
import com.example.springdatapoc.service.AsyncService;
import com.example.springdatapoc.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.transaction.Transactional;
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