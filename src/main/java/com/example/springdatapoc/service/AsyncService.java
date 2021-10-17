package com.example.springdatapoc.service;

import com.example.springdatapoc.components.DirectToDb;
import com.example.springdatapoc.repository.Person;
import com.example.springdatapoc.repository.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
  @NonNull
  @Autowired
  private PersonRepository repository;

  @NonNull
  @Lazy
  private DirectToDb runner;

  static private long saveTime = 0;
//  @Async
//  public void asyncSaveAll() {
//    while(!Thread.currentThread().isInterrupted() && runner.exit == false) {
////      System.out.println("############################### ASYNC THREAD ###############################");
////      runner.savePersons();
//      var people = runner.getPeople();
//      if(people != null && !people.isEmpty()) {
//        System.out.println("############################### SAVING PERSONS ###############################");
//        repository.saveAll(people);
//      }
//      try {
//        Thread.sleep(3000,0);
//      } catch(InterruptedException ex) {
//        Thread.currentThread().interrupt();
//      }
//    }
//
//    if (runner.exit == false) {
//      Thread.currentThread().interrupt();
//    }
//  }

  @Async
  public void asyncSaveCoherence() {
    while(!Thread.currentThread().isInterrupted() && runner.exit == false) {
//      System.out.println("############################### ASYNC THREAD ###############################");
//      runner.savePersons();
      var people = runner.getPeople();
      if(people != null && !people.isEmpty()) {
        System.out.println("############################### SAVING PERSONS ###############################");
        repository.saveAll(people);
      }
      try {
        Thread.sleep(3000,0);
      } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }

    if (runner.exit == false) {
      Thread.currentThread().interrupt();
    }
  }

  @Async
  public void asyncSaveAll(Semaphore semaphore) {
    while(!Thread.currentThread().isInterrupted() && runner.exit == false) {
      List<Person> people = new ArrayList<Person>();
      try {
        semaphore.acquire();
        try {
          people.addAll(runner.getPeople());
          if(people != null && !people.isEmpty()) {
            runner.clearPeople();
          }
        } finally {
          semaphore.release();
        }
        if(people != null && !people.isEmpty()) {
          System.out.println("############################### SAVING PERSONS ###############################");
          Logger log = Logger.getLogger("################################## DirectToDb");
          long startTime = System.nanoTime();
          repository.saveAll(people);
          long endTime = System.nanoTime();
          long totalTime = endTime - startTime;
          log.info("Saving new people to DB took: " + TimeUnit.NANOSECONDS.toMillis(totalTime));
          log.info("Saving ALL persons to DB took: " + TimeUnit.NANOSECONDS.toMillis(saveTime+=totalTime));
        }
      } catch(final InterruptedException ie) {
        System.out.println(ie.toString());
      }
//      try {
//        Thread.sleep(10000,0);
//      } catch(InterruptedException ex) {
//        Thread.currentThread().interrupt();
//      }
    }
//    if (runner.exit == false) {
//      Thread.currentThread().interrupt();
//    }
  }

  public AsyncService(@NonNull final PersonRepository repository, @NonNull @Lazy DirectToDb runner) {
    if (repository == null) {
      throw new NullPointerException("repository is marked non-null but is null");
    } else {
      this.repository = repository;
    }
    if (runner == null) {
      throw new NullPointerException("asyncService is marked non-null but is null");
    } else {
      this.runner = runner;
    }
  }
}
