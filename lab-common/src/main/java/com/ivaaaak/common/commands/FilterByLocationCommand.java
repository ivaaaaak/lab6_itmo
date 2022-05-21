package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;


import java.util.List;
import java.util.StringJoiner;

public class FilterByLocationCommand extends Command {
    private final Location locationArg;

    public FilterByLocationCommand(Location location) {
        locationArg = location;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {

        List<Person> list = collectionStorage.getMatchingPeople(locationArg);
        StringJoiner output = new StringJoiner("\n\n");
        for (Person person : list) {
            output.add(person.toString());
        }
        if (output.toString().isEmpty()) {
            return new CommandResult("There aren't any elements with this location");
        }
        return new CommandResult(output.toString());
    }
}
