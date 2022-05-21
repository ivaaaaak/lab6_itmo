package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;

public class ReplaceIfLowerCommand extends Command {
    private final Integer key;
    private final Person person;

    public ReplaceIfLowerCommand(Integer key, Person person) {
        this.key = key;
        this.person = person;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.containsKey(key)) {

            if (collectionStorage.replaceIfNewLower(key, person)) {
                return new CommandResult("The element has been replaced");
            }
            return new CommandResult("The element is lower than a new one or equal");
        }
        return new CommandResult("Collection doesn't contain this key");

    }
}
