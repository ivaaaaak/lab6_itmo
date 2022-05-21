package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.ivaaaak.common.util.Serializing.deserialize;
import static com.ivaaaak.common.util.Serializing.serialize;

public final class ClientExchanger {

    static final int MAX_META_DATA = 4;

    private ClientExchanger() {

    }

    public static SocketChannel openChannelToServer(String host, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        InetAddress ia = InetAddress.getByName(host);
        InetSocketAddress address = new InetSocketAddress(ia, port);
        channel.connect(address);
        System.out.println("Connection has been established");
        return channel;
    }

    public static SocketChannel reconnectToServer(String host, int port) {
        try {
            System.err.println("Failed to exchange data with server. Server isn't working");
            return ClientExchanger.openChannelToServer(host, port);
        } catch (IOException e) {
            return null;
        }
    }

    public static void sendCommand(Command command, SocketChannel channel) throws IOException {
        byte[] serializedCommand = serialize(command);
        int commandSize = serializedCommand.length;

        ByteBuffer mainData = ByteBuffer.wrap(serializedCommand);
        ByteBuffer metaData = ByteBuffer.allocate(MAX_META_DATA).putInt(commandSize);
        metaData.position(0);
        channel.write(metaData);
        channel.write(mainData);
        metaData.clear();
        mainData.clear();
    }

    public static CommandResult receiveResult(SocketChannel channel) throws IOException, ClassNotFoundException {
        ByteBuffer metaData = ByteBuffer.allocate(MAX_META_DATA);
        channel.read(metaData);
        metaData.position(0);
        ByteBuffer mainData = ByteBuffer.allocate(metaData.getInt());
        metaData.clear();
        channel.read(mainData);
        CommandResult result = (CommandResult) deserialize(mainData.array());
        mainData.clear();
        return result;
    }



}
