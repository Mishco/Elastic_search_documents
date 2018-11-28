package model;

import org.joda.time.LocalDate;

import java.util.UUID;

public class Person {

    private String personId;
    private String name;
    private String fullName;
    private LocalDate dateOfBirth;
    private String email;
    private String telNumber;


    public Person() {
    }

    public Person(String name) {
        this.personId = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonNumber() {
        return telNumber;
    }

    public void setTelefonNumber(String telefonNumber) {
        this.telNumber = telefonNumber;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }
}
