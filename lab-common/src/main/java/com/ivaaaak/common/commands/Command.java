package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

import java.io.Serializable;

public abstract class Command implements Serializable {

    public abstract CommandResult execute(CollectionStorable collectionStorage);

}
