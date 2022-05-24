package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.util.CollectionStorable;

public class FilterByLocationCommand extends Command {
    private final Location locationArg;

    public FilterByLocationCommand(Location location) {
        locationArg = location;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        Object[] answer = collectionStorage.getMatchingPeople(locationArg);
        if (answer.length == 0) {
            return new CommandResult("There aren't any elements with this location");
        }
        return new CommandResult(answer);
    }
}
