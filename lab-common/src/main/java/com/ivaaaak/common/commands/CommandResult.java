package com.ivaaaak.common.commands;

import java.io.Serializable;

public class CommandResult implements Serializable {

    private final String message;

    public CommandResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CommandResult{"
                + "message='"
                + message
                + '\''
                + '}';
    }
}
