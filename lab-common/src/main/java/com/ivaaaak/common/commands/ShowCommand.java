package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

import java.util.StringJoiner;

public class ShowCommand extends Command {

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.getHashtable().isEmpty()) {
            return new CommandResult("The collection is empty");
        }
        StringJoiner output = new StringJoiner("\n\n");
        for (Integer key : collectionStorage.getKeysSet()) {
            output.add(key + " = " + collectionStorage.getPerson(key).toString());
        }
        return new CommandResult(output.toString());
    }

}
