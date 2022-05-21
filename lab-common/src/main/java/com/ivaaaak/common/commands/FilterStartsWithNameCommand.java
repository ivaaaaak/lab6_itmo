package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;

import java.util.List;
import java.util.StringJoiner;


public class FilterStartsWithNameCommand extends Command {
    private final String stringArg;

    public FilterStartsWithNameCommand(String arg) {
        stringArg = arg;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {

        List<Person> list = collectionStorage.getMatchingPeople(stringArg);
        StringJoiner output = new StringJoiner("\n\n");
        for (Person person : list) {
            output.add(person.toString());
        }
        if (output.toString().isEmpty()) {
            return new CommandResult("There aren't any elements whose name starts like this");
        }
        return new CommandResult(output.toString());

    }
}
