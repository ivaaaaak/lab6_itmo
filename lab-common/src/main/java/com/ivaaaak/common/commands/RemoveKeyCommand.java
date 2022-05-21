package com.ivaaaak.common.commands;


import com.ivaaaak.common.util.CollectionStorable;

public class RemoveKeyCommand extends Command {
    private final Integer key;

    public RemoveKeyCommand(Integer key) {
        this.key = key;
    }

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.containsKey(key)) {
            collectionStorage.remove(key);
            return new CommandResult("The element has been removed");
        }
        return new CommandResult("Collection doesn't contain this key");
    }
}
