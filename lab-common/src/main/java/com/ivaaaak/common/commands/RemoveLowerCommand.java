package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;

public class RemoveLowerCommand extends Command {
    private final Person person;

    public RemoveLowerCommand(Person person) {
        this.person = person;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        collectionStorage.removeLowerPeople(person);
        return new CommandResult("Lower elements were removed");
    }
}
