package com.ivaaaak.server;

import static com.ivaaaak.common.util.Serializing.deserialize;
import static com.ivaaaak.common.util.Serializing.serialize;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;


public final class ServerExchanger {

    private static final int MAX_META_DATA = 4;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerExchanger.class);
    private static final int SERVER_WAITING_TIME = 5;

    private ServerExchanger() {

    }

    public static ServerSocketChannel openChannel(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        serverChannel.bind(address);
        LOGGER.info("Listening to the port: " + address.getPort());
        serverChannel.configureBlocking(false);
        return serverChannel;
    }

    public static Selector openSelector(ServerSocketChannel serverChannel) throws IOException {
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        return selector;
    }

    private static Command read(SelectionKey key) throws IOException, ClassNotFoundException {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer metaData = ByteBuffer.allocate(MAX_META_DATA);

            if (channel.read(metaData) != -1) {
                metaData.position(0);
                int commandSize = metaData.getInt();
                ByteBuffer mainData = ByteBuffer.allocate(commandSize);
                TimeUnit.MILLISECONDS.sleep(SERVER_WAITING_TIME);
                if (channel.read(mainData) != 0) {
                    Command command = (Command) deserialize(mainData.array());
                    mainData.clear();
                    channel.configureBlocking(false);
                    channel.register(key.selector(), SelectionKey.OP_WRITE);
                    return command;
                }
            }
        } catch (IllegalArgumentException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void write(SelectionKey key, CommandResult result) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        byte[] serializedResult = serialize(result);
        int resultSize = serializedResult.length;

        ByteBuffer mainData = ByteBuffer.wrap(serializedResult);
        ByteBuffer metaData = ByteBuffer.allocate(MAX_META_DATA).putInt(resultSize);
        metaData.position(0);
        channel.write(metaData);
        channel.write(mainData);
        metaData.clear();
        mainData.clear();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ);
    }

    public static void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ);
        LOGGER.info("Established connection with client: " + channel.getLocalAddress());
    }

    public static void readAndExecuteCommand(SelectionKey key, CollectionStorage collectionStorage) throws IOException, ClassNotFoundException {
        Command currentCommand = read(key);
        if (currentCommand != null) {
            key.attach(currentCommand.execute(collectionStorage));
            LOGGER.info("Read a command from client: " + currentCommand);
        }
    }

    public static void writeResult(SelectionKey key) throws IOException {
        CommandResult result = (CommandResult) key.attachment();
        write(key, result);
        LOGGER.info("Sent result to client: " + result.toString());
    }

}
