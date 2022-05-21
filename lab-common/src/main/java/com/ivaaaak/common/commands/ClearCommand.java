package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

public class ClearCommand extends Command {

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        collectionStorage.clear();
        return new CommandResult("Collection has been cleared");
    }
}
