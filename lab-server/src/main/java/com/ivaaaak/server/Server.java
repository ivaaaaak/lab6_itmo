package com.ivaaaak.server;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.FileManager;
import com.ivaaaak.server.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public final class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        try {
            int serverPort = Integer.parseInt(args[0]);

            try (ServerSocketChannel serverChannel = ServerExchanger.openChannel(serverPort);
                 Selector selector = ServerExchanger.openSelector(serverChannel)) {
                startCycle(selector);
            } catch (IOException e) {
                LOGGER.error("Failed to open server's channel or its selector");
                e.printStackTrace();
            }

        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            LOGGER.error("Invalid port. You must enter port as an integer argument");
        }
    }

    private static void startCycle(Selector selector) {
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
                    serveClients(selector, collectionStorage);
                } catch (IOException e) {
                    LOGGER.error("Some IO error with selector has occurred");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Something's wrong with server's console output");
            e.printStackTrace();
        }
    }

    private static void serveClients(Selector selector, CollectionStorage collectionStorage) throws IOException {
        selector.wakeup();
        selector.select();
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();

        while (iterator.hasNext()) {
            processKey(iterator.next(), collectionStorage);
            iterator.remove();
        }
    }

    private static void processKey(SelectionKey key, CollectionStorage collectionStorage) {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                try {
                    ServerExchanger.accept(key);
                } catch (IOException e) {
                    LOGGER.error("Failed to open new client's channel");
                    e.printStackTrace();
                }
            }
            if (key.isReadable()) {
                try {
                    ServerExchanger.readAndExecuteCommand(key, collectionStorage);
                } catch (IOException e) {
                    LOGGER.error("Failed to read command");
                    e.printStackTrace();
                    key.cancel();
                    return;
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Server received incorrect data from client");
                    e.printStackTrace();
                }
            }
            if (key.isWritable()) {
                try {
                    ServerExchanger.writeResult(key);
                } catch (IOException e) {
                    LOGGER.error("Failed to send the result to client");
                    e.printStackTrace();
                }
            }
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
            String fileData = FileManager.read(FileManager.getMainFilePath());
            Hashtable<Integer, Person> ht = JsonParser.parseFromString(fileData);
            collectionStorage.initializeHashtable(ht);
            return true;
        } catch (NullPointerException e) {
            LOGGER.error("You need to create the environment variable LAB with a path to a file where collection will be saved");
            return false;
        } catch (IOException e) {
            LOGGER.error("Failed to read the file with the collection");
            e.printStackTrace();
            return false;
        }
    }
}
