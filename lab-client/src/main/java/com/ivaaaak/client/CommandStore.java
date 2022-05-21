package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.HelpCommand;
import com.ivaaaak.common.commands.InsertCommand;
import com.ivaaaak.common.commands.ShowCommand;
import com.ivaaaak.common.commands.InfoCommand;
import com.ivaaaak.common.commands.FilterStartsWithNameCommand;
import com.ivaaaak.common.commands.RemoveKeyCommand;
import com.ivaaaak.common.commands.ReplaceIfGreaterCommand;
import com.ivaaaak.common.commands.ReplaceIfLowerCommand;
import com.ivaaaak.common.commands.ClearCommand;
import com.ivaaaak.common.commands.UpdateCommand;
import com.ivaaaak.common.commands.RemoveLowerCommand;
import com.ivaaaak.common.commands.MaxByHairColorCommand;
import com.ivaaaak.common.commands.FilterByLocationCommand;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

import java.util.HashSet;

public final class CommandStore {
    private static final HashSet<String> COMMAND_NAMES = new HashSet<>();

    private CommandStore() {
    }

    static {
        COMMAND_NAMES.add("clear");
        COMMAND_NAMES.add("filter_by_location");
        COMMAND_NAMES.add("filter_starts_with_name");
        COMMAND_NAMES.add("help");
        COMMAND_NAMES.add("info");
        COMMAND_NAMES.add("insert");
        COMMAND_NAMES.add("max_by_hair_color");
        COMMAND_NAMES.add("remove_key");
        COMMAND_NAMES.add("remove_lower");
        COMMAND_NAMES.add("replace_if_greater");
        COMMAND_NAMES.add("replace_if_lower");
        COMMAND_NAMES.add("show");
        COMMAND_NAMES.add("update");
    }

    @SuppressWarnings("ReturnCount")
    public static Command getCommandObject(String name, Object arg1, Object arg2) {
        switch (name) {
            case "clear":
                return new ClearCommand();
            case "filter_by_location":
                return new FilterByLocationCommand((Location) arg1);
            case "filter_starts_with_name":
                return new FilterStartsWithNameCommand((String) arg1);
            case "info":
                return new InfoCommand();
            case "insert":
                return new InsertCommand((Integer) arg1, (Person) arg2);
            case "max_by_hair_color":
                return new MaxByHairColorCommand();
            case "remove_key":
                return new RemoveKeyCommand((Integer) arg1);
            case "remove_lower":
                return new RemoveLowerCommand((Person) arg1);
            case "replace_if_greater":
                return new ReplaceIfGreaterCommand((Integer) arg1, (Person) arg2);
            case "replace_if_lower":
                return new ReplaceIfLowerCommand((Integer) arg1, (Person) arg2);
            case "show":
                return new ShowCommand();
            case "update":
                return new UpdateCommand((Integer) arg1, (Person) arg2);
            default:
                return new HelpCommand();
        }
    }

    public static HashSet<String> getCommandsNames() {
        return COMMAND_NAMES;
    }
}
