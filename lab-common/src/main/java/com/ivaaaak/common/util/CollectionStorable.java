package com.ivaaaak.common.util;

import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public interface CollectionStorable {

    void initializeHashtable(Hashtable<Integer, Person> fileHashtable);
    LocalDate getCreationDate();
    Hashtable<Integer, Person> getHashtable();
    Integer getMaxId();
    void clear();
    void remove(Integer key);
    void add(Integer key, Person person);
    void replace(Integer key, Person newPerson);
    List<Person> getMatchingPeople(Location location);
    List<Person> getMatchingPeople(String substring);
    Person getMaxColorPerson();
    void removeLowerPeople(Person person);
    boolean replaceIfNewGreater(Integer oldKey, Person newPerson);
    boolean replaceIfNewLower(Integer oldKey, Person newPerson);
    boolean containsKey(Integer key);
    Integer getMatchingIDKey(Integer id);
    Set<Integer> getKeysSet();
    Person getPerson(Integer key);
}