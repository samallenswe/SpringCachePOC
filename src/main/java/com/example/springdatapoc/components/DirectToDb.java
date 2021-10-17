package com.example.springdatapoc.components;

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

@Order(1)
@Component
public class DirectToDb implements CommandLineRunner {
  @NonNull
  private PersonRepository repository;

  @NonNull
  private AsyncService asyncService;

  public static boolean exit = false;

  private List<Person> people;

  public List<Person> getPeople() {
    return people;
  }

  public void clearPeople() {
    people.clear();
  }

  @Override
  public void run(String... args) throws Exception {
//    Semaphore semaphore = new Semaphore(1);
//    asyncService.asyncSaveAll(semaphore);
////    asyncService.asyncSaveAll();
//    createPersons(semaphore);
  }

  public void createPersons(Semaphore semaphore) {
    for (int i = 1; i < 200; i++) {
      Person person = Utils.createRandomPerson(i);
      try {
        semaphore.acquire();
        try {
          System.out.println("Created new person: " + person);
          people.add(person);
        } finally {
          semaphore.release();
        }
      } catch(final InterruptedException ie) {
        System.out.println(ie.toString());
      }
      try {
        Thread.sleep(200,0);
      } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @Transactional
  public void savePersons() {
    Logger log = Logger.getLogger("################################## DirectToDb");
    long startTime = System.nanoTime();
    if(!people.isEmpty()) {
      repository.saveAll(people);
    }
    long endTime = System.nanoTime();
    log.info("Saving all persons to DB took: " + TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
  }

  public DirectToDb(@NonNull final PersonRepository repository, @NonNull AsyncService asyncService) {
    this.people = new ArrayList<Person>();

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
