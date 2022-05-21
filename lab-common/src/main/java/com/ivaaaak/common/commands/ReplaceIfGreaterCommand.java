package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;


public class ReplaceIfGreaterCommand extends Command {
    private final Integer key;
    private final Person person;

    public ReplaceIfGreaterCommand(Integer key, Person person) {
        this.key = key;
        this.person = person;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.containsKey(key)) {

            if (collectionStorage.replaceIfNewGreater(key, person)) {
                return new CommandResult("The element has been replaced");
            }
            return new CommandResult("The element is greater than a new one or equal");
        }
        return new CommandResult("Collection doesn't contain this key");
    }
}
