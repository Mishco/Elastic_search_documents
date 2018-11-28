package model;

import java.util.ArrayList;
import java.util.List;

public class People {

    private List<Person> personList;

    public People() {
        this.personList = new ArrayList<>();
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public Person addToList(Person p) {
        this.personList.add(p);
        return p;
    }

    public Person removeFromList(Person p) {
        this.personList.remove(p);
        return p;
    }

    public boolean isListPeopleEmpty() {
        return personList.isEmpty();
    }

    public int sizeOfpersonList() {
        return personList.size();
    }


    @Override
    public String toString() {
        return "People{" +
                "personList=" + personList +
                '}';
    }
}
