package com.ivaaaak.server;

import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class CollectionStorage implements com.ivaaaak.common.util.CollectionStorable {
    private Hashtable<Integer, Person> hashtable = new Hashtable<>();
    private LocalDate creationDate;

    public Integer getMaxId() {
        int maxID = 0;
        Comparator<Person> comparator = Comparator.comparingInt(Person::getId);
        Optional<Person> maxIDPerson = hashtable.values().stream().max(comparator);
        if (maxIDPerson.isPresent()) {
            maxID = maxIDPerson.get().getId();
        }
        return maxID;
    }

    public void initializeHashtable(Hashtable<Integer, Person> fileHashtable) {
        creationDate = LocalDate.now();
        hashtable = fileHashtable;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Hashtable<Integer, Person> getHashtable() {
        return hashtable;
    }

    public void add(Integer key, Person person) {
        person.setId(getMaxId() + 1);
        hashtable.put(key, person);
    }

    public void replace(Integer key, Person newPerson) {
        Person oldPerson = hashtable.get(key);
        newPerson.setId(oldPerson.getId());
        hashtable.replace(key, oldPerson, newPerson);
    }

    public void clear() {
        hashtable.clear();
    }

    public void remove(Integer key) {
        hashtable.remove(key);
    }



    public Person getPerson(Integer key) {
        return hashtable.get(key);
    }

    public Set<Integer> getKeysSet() {
        return hashtable.keySet();
    }

    public boolean containsKey(Integer key) {
        return hashtable.containsKey(key);
    }


    public List<Person> getMatchingPeople(Location location) {
        return hashtable.values().stream().filter(x -> (x.getLocation() != null)).filter(x -> (x.getLocation().equals(location))).collect(Collectors.toList());
    }

    public List<Person> getMatchingPeople(String substring) {
        return hashtable.values().stream().filter(x -> x.getName().startsWith(substring)).collect(Collectors.toList());
    }

    public Person getMaxColorPerson() {
        Comparator<Person> comparator = Comparator.comparing(Person::getHairColor);
        Optional<Person> maxPerson = hashtable.values().stream().max(comparator);
        return maxPerson.orElse(null);
    }

    public void removeLowerPeople(Person person) {
        hashtable.entrySet().removeIf(x -> x.getValue().compareTo(person) < 0);
    }

    public boolean replaceIfNewGreater(Integer oldKey, Person newPerson) {
        Person oldPerson = hashtable.get(oldKey);
        if (oldPerson.compareTo(newPerson) < 0) {
            newPerson.setId(getMaxId() + 1);
            hashtable.replace(oldKey, oldPerson, newPerson);
            return true;
        }
        return false;

    }
    public boolean replaceIfNewLower(Integer oldKey, Person newPerson) {
        Person oldPerson = hashtable.get(oldKey);
        if (oldPerson.compareTo(newPerson) > 0) {
            newPerson.setId(getMaxId() + 1);
            hashtable.replace(oldKey, oldPerson, newPerson);
            return true;
        }
        return false;
    }

    public Integer getMatchingIDKey(Integer id) {
        Optional<Map.Entry<Integer, Person>> element = hashtable.entrySet().stream().
                filter(x -> x.getValue().getId().equals(id)).
                findFirst();
        return element.map(Map.Entry::getKey).orElse(null);
    }
}
