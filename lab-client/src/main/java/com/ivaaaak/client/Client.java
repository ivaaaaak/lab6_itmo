package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.StringJoiner;
import java.util.function.Function;

public final class Client {
    private static ClientExchanger clientExchanger;
    private static final InputManager INPUT_MANAGER = new InputManager();
    private static final PersonMaker PERSON_MAKER = new PersonMaker(INPUT_MANAGER);

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            clientExchanger = new ClientExchanger(host, port);
            try (SocketChannel channel = clientExchanger.openChannelToServer()) {
                clientExchanger.setSocketChannel(channel);
                startCycle();
            } catch (IOException e) {
                System.err.println("Failed to open channel with server. There isn't working server on these host and port.");
                e.printStackTrace();
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Cannot parse host and port arguments. Enter them in the order: host's name, port");
        }
    }

    private static void startCycle() {
        while (true) {
            String[] command = INPUT_MANAGER.readLine().split(" ");
            String name = command[0];
            String arg = "";
            if (command.length > 1) {
                arg = command[1];
            }
            if ("exit".equals(name)) {
                break;
            }
            if ("execute_script".equals(name)) {
                if (checkArgument(arg, x -> x) != null) {
                    INPUT_MANAGER.connectToFile(arg);
                    continue;
                }
            }
            if (CommandStore.getCommandsNames().contains(name)) {
                Command currentCommand = formCurrentCommand(name, arg);
                if (currentCommand != null) {
                    processCommand(currentCommand);
                }
            } else {
                System.out.println("Command not found. Use \"help\" to get information about commands");
            }
        }
    }

    private static void processCommand(Command command) {
        try {
            clientExchanger.sendCommand(command);
            CommandResult result = clientExchanger.receiveResult();
            String message = result.getMessage();
            Object[] answer = result.getPeople();
            if (message != null) {
                System.out.println(message);
            }
            if (answer != null) {
                StringJoiner output = new StringJoiner("\n\n");
                for (Object person : answer) {
                    output.add(person.toString());
                }
                System.out.println(output);
            }
        } catch (IOException e) {
            System.err.println("Failed to process command: " + command.toString());
            e.printStackTrace();
            SocketChannel newChanel = clientExchanger.reconnectToServer();
            if (newChanel != null) {
                clientExchanger.setSocketChannel(newChanel);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Incorrect answer from server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static <T> T checkArgument(String arg, Function<String, T> converter) {
        if (arg.isBlank()) {
            System.out.println("This command needs an argument. Please try again:");
            return null;
        }
        try {
            return converter.apply(arg);
        } catch (IllegalArgumentException e) {
            System.out.println("Argument is an integer number. Use \"show\" to get information about elements\n");
            return null;
        }
    }

    private static Command formCurrentCommand(String name, String arg) {
        Object firstArgument = null;
        Object secondArgument = null;

        if ("insert".equals(name) || "update".equals(name) || "replace_if_greater".equals(name)
                || "replace_if_lower".equals(name)) {
            firstArgument = checkArgument(arg, Integer::parseInt);
            if (firstArgument == null) {
                return null;
            }
            secondArgument = PERSON_MAKER.makePerson();
        }
        if ("remove_lower".equals(name)) {
            firstArgument = PERSON_MAKER.makePerson();
        }
        if ("remove_key".equals(name)) {
            firstArgument = checkArgument(arg, Integer::parseInt);
            if (firstArgument == null) {
                return null;
            }
        }
        if ("filter_by_location".equals(name)) {
            firstArgument = PERSON_MAKER.makeLocation();
        }
        if ("filter_starts_with_name".equals(name)) {
            firstArgument = checkArgument(arg, x -> x);
            if (firstArgument == null) {
                return null;
            }
        }
        return CommandStore.getCommandObject(name, firstArgument, secondArgument);
    }

}
