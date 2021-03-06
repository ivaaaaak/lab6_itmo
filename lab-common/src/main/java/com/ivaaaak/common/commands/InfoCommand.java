package com.ivaaaak.common.commands;


import com.ivaaaak.common.util.CollectionStorable;

public class InfoCommand extends Command {

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        return new CommandResult("Тип коллекции: "
                + collectionStorage.getHashtable().getClass().toString() + "\n"
                + "Число элементов: " + collectionStorage.getHashtable().size() + "\n"
                + "Дата создания: " + collectionStorage.getCreationDate());

    }
}
