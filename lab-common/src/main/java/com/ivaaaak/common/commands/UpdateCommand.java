package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;

public class UpdateCommand extends Command {
    private final Integer id;
    private final Person newPerson;

    public UpdateCommand(Integer id, Person person) {
        this.id = id;
        newPerson = person;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        Integer key = collectionStorage.getMatchingIDKey(id);
        if (key != null) {
            collectionStorage.replace(key, newPerson);
            return new CommandResult("The element has been updated");
        }
        return new CommandResult("There's no element with this id. Use \"show\" to get information about elements");
    }
}
