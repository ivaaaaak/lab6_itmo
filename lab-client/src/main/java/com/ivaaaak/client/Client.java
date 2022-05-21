package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.function.Function;

public final class Client {

    private static String host;
    private static int port;
    private static SocketChannel currentChannel;

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            try (SocketChannel channel = ClientExchanger.openChannelToServer(host, port)) {
                final InputManager inputManager = new InputManager();
                final PersonMaker personMaker = new PersonMaker(inputManager);
                startCycle(inputManager, personMaker, channel);
            } catch (IOException e) {
                System.err.println("Failed to open channel with server. There isn't working server on these host and port.");
                e.printStackTrace();
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Cannot parse host and port arguments. Enter them in the order: host's name, port");
        }
    }

    private static void startCycle(InputManager inputManager,
                                   PersonMaker personMaker,
                                   SocketChannel channel) {
        currentChannel = channel;
        while (true) {
            String[] command = inputManager.readLine().split(" ");
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
                    inputManager.connectToFile(arg);
                    continue;
                }
            }
            if (CommandStore.getCommandsNames().contains(name)) {
                Command currentCommand = formCurrentCommand(name, arg, personMaker);
                if (currentCommand != null) {
                    processCommand(currentCommand, currentChannel);
                }
            } else {
                System.out.println("Command not found. Use \"help\" to get information about commands");
            }
        }
    }

    private static void processCommand(Command command, SocketChannel channel) {
        try {
            ClientExchanger.sendCommand(command, channel);
            CommandResult result = ClientExchanger.receiveResult(channel);
            System.out.println(result.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            SocketChannel newChanel = ClientExchanger.reconnectToServer(host, port);
            if (newChanel != null) {
                currentChannel = newChanel;
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Incorrect answer from server");
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

    private static Command formCurrentCommand(String name, String arg, PersonMaker personMaker) {
        Object firstArgument = null;
        Object secondArgument = null;

        if ("insert".equals(name) || "update".equals(name) || "replace_if_greater".equals(name)
                || "replace_if_lower".equals(name)) {
            firstArgument = checkArgument(arg, Integer::parseInt);
            if (firstArgument == null) {
                return null;
            }
            secondArgument = personMaker.makePerson();
        }
        if ("remove_lower".equals(name)) {
            firstArgument = personMaker.makePerson();
        }
        if ("remove_key".equals(name)) {
            firstArgument = checkArgument(arg, Integer::parseInt);
            if (firstArgument == null) {
                return null;
            }
        }
        if ("filter_by_location".equals(name)) {
            firstArgument = personMaker.makeLocation();
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
