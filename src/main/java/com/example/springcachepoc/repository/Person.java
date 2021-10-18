package com.example.springcachepoc.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "firstName")
  private String firstName;

  @Column(name = "lastName")
  private String lastName;

  @Column(name = "sex")
  private String sex;

  public Person(String firstName, String lastName, String sex) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.sex = sex;
  }

  public Person() {

  }

  @Override
  public String toString() {
    return "Person{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        '}';
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }
}
