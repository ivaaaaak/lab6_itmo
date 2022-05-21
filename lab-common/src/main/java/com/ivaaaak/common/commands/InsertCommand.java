package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;


public class InsertCommand extends Command {
    private final Integer key;
    private final Person newPerson;

    public InsertCommand(Integer key, Person person) {
        this.key = key;
        newPerson = person;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.containsKey(key)) {
            return new CommandResult("Collection already have an element with this key");
        }
        collectionStorage.add(key, newPerson);
        return new CommandResult("The element has been added");
    }

}
