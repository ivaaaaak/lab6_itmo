package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;


public class MaxByHairColorCommand extends Command {

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        Person maxPerson = collectionStorage.getMaxColorPerson();
        Person[] answer = new Person[] {maxPerson};
        if (maxPerson == null) {
            return new CommandResult("The collection is empty");
        }
        return new CommandResult(answer);
    }
}
