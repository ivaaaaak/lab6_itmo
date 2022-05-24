package com.ivaaaak.server;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.FileManager;
import com.ivaaaak.server.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Scanner;

public final class Server {
    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final ServerExchanger SERVER_EXCHANGER = new ServerExchanger();

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            int serverPort = Integer.parseInt(args[0]);

            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                SERVER_EXCHANGER.setServerSocket(serverSocket);
                startCycle();
            } catch (IOException e) {
                LOGGER.error("Failed to open server socket: ", e);
            }

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Invalid port. You must enter port as an integer argument");
        }
    }

    private static void startCycle() {
        final CollectionStorage collectionStorage = new CollectionStorage();

        if (!getHashtableFromFile(collectionStorage)) {
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (System.in.available() > 0) {
                    String input = scanner.nextLine();
                    if ("exit".equals(input)) {
                        saveHashtableToFile(collectionStorage);
                        break;
                    }
                    if ("save".equals(input)) {
                        saveHashtableToFile(collectionStorage);
                        LOGGER.info("Collection has been saved");
                    }
                }
                try {
                    serveClients(collectionStorage);
                } catch (IOException e) {
                    LOGGER.error("Failed to close client socket: ", e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Something's wrong with server's console output: ", e);
        }
    }

    private static void serveClients(CollectionStorage collectionStorage) throws IOException {
        acceptNewClients();
        for (Socket clientSocket: SERVER_EXCHANGER.getClients()) {
            try {
                Command command = SERVER_EXCHANGER.receiveCommand(clientSocket);
                if (command != null) {
                    CommandResult result = command.execute(collectionStorage);
                    SERVER_EXCHANGER.sendResult(clientSocket, result);
                }
            } catch (SocketException e) {
                LOGGER.error("There's problem with client socket: ", e);
                clientSocket.close();
                SERVER_EXCHANGER.removeClient(clientSocket);
            } catch (IOException e) {
                LOGGER.error("Failed to process the command: ", e);
                clientSocket.close();
                SERVER_EXCHANGER.removeClient(clientSocket);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Received invalid data from client", e);
            }
        }
    }

    private static void acceptNewClients() {
        try {
            Socket clientSocket = SERVER_EXCHANGER.acceptConnection();
            if (clientSocket != null) {
                SERVER_EXCHANGER.addClient(clientSocket);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to open new client socket: ", e);
        }
    }

    private static void saveHashtableToFile(CollectionStorage collectionStorage) {
        try {
            String data = JsonParser.parseIntoString(collectionStorage.getHashtable());
            FileManager.write(data, FileManager.getMainFilePath());
        } catch (IOException e) {
            LOGGER.error("Failed to save collection into the file");
            e.printStackTrace();
        }
    }

    private static boolean getHashtableFromFile(CollectionStorage collectionStorage) {
        try {
            FileManager.setMainFilePath(System.getenv("LAB"));
            if (FileManager.getMainFilePath().isBlank()) {
                LOGGER.error("You need to create the environment variable LAB with a path to a file where collection will be saved");
                return false;
            }
            String fileData = FileManager.read(FileManager.getMainFilePath());
            Hashtable<Integer, Person> ht = JsonParser.parseFromString(fileData);
            collectionStorage.initializeHashtable(ht);
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to read the file with the collection");
            e.printStackTrace();
            return false;
        }
    }
}
