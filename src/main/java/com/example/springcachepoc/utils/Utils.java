package com.example.springcachepoc.utils;

import com.example.springcachepoc.repository.Person;

public class Utils {
  public static final int TEST_SIZE = 1000;

  public static Person createRandomPerson(int i) {
    Person person = new Person();
    person.setId((long)i);
    person.setFirstName("firstName" + i);
    person.setLastName("lastName" + i);
    if( i % 2 == 0) {
      person.setSex("male");
    } else {
      person.setSex("female");
    }
    return person;
  }
}
