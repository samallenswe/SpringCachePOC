package com.example.springcachepoc.utils;

import com.example.springcachepoc.repository.Person;

public class Utils {
  public static final int TEST_SIZE = 500;
  public static final int POOL_SIZE = 5;

  public static Person createRandomPerson(int i) {
    Person person = new Person();
    person.setFirstName("firstName" + i);
    person.setLastName("lastName" + i);
    return person;
  }
}
