package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

public class FilterStartsWithNameCommand extends Command {
    private final String stringArg;

    public FilterStartsWithNameCommand(String arg) {
        stringArg = arg;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        Object[] answer = collectionStorage.getMatchingPeople(stringArg);
        if (answer.length == 0) {
            return new CommandResult("There aren't any elements whose name starts like this");
        }
        return new CommandResult(answer);
    }
}
