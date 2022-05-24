package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import static com.ivaaaak.common.util.Serializing.deserialize;
import static com.ivaaaak.common.util.Serializing.serialize;

public class ClientExchanger {

    private final String host;
    private final int port;
    private final int maxMetaData = 4;
    private SocketChannel channel;

    public ClientExchanger(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SocketChannel openChannelToServer() throws IOException {
        SocketChannel newChannel = SocketChannel.open();
        InetAddress ia = InetAddress.getByName(host);
        InetSocketAddress address = new InetSocketAddress(ia, port);
        newChannel.connect(address);
        System.out.println("Connection has been established");
        newChannel.configureBlocking(false);
        return newChannel;
    }

    public void setSocketChannel(SocketChannel newChannel) {
        channel = newChannel;
    }

    public SocketChannel reconnectToServer() {
        try {
            System.err.println("Failed to exchange data with server. Server isn't working");
            return openChannelToServer();
        } catch (IOException e) {
            return null;
        }
    }

    public void sendCommand(Command command) throws IOException {
        byte[] serializedCommand = serialize(command);
        int commandSize = serializedCommand.length;

        ByteBuffer mainData = ByteBuffer.wrap(serializedCommand);
        ByteBuffer metaData = ByteBuffer.allocate(maxMetaData).putInt(commandSize);
        metaData.position(0);
        channel.write(metaData);
        channel.write(mainData);
    }

    public CommandResult receiveResult() throws IOException, ClassNotFoundException, InterruptedException {
        ByteBuffer metaData = ByteBuffer.allocate(maxMetaData);
        final int clientWaitingPeriod = 10;
        int waitingTime = clientWaitingPeriod;
        while (waitingTime > 0) {
            if (channel.read(metaData) != 0) {
                metaData.position(0);
                ByteBuffer mainData = ByteBuffer.allocate(metaData.getInt());
                channel.read(mainData);
                return (CommandResult) deserialize(mainData.array());
            }
            waitingTime--;
            TimeUnit.MILLISECONDS.sleep(clientWaitingPeriod);
        }
        return new CommandResult("Waiting time for the response from server has expired", null);
    }



}
