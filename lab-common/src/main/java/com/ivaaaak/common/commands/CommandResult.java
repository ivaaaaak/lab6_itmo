package com.ivaaaak.common.commands;

import java.io.Serializable;

public class CommandResult implements Serializable {

    private final String message;
    private final Object[] people;

    public CommandResult(String message, Object[] people) {
        this.message = message;
        this.people = people;
    }
    public CommandResult(String message) {
        this.message = message;
        this.people = null;
    }
    public CommandResult(Object[] people) {
        this.people = people;
        this.message = "";
    }

    public String getMessage() {
        return message;
    }

    public Object[] getPeople() {
        return people;
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
