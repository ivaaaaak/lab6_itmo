package com.ivaaaak.server;

import static com.ivaaaak.common.util.Serializing.deserialize;
import static com.ivaaaak.common.util.Serializing.serialize;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashSet;


public class ServerExchanger {

    private static final Logger LOGGER = Server.LOGGER;
    private final int maxMetaData = 4;
    private ServerSocket serverSocket;
    private final HashSet<Socket> clients = new HashSet<>();
    private final int serverWaitingPeriod = 10;

    public ServerExchanger() {

    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        LOGGER.info("Listening to the port: {}", serverSocket.getLocalPort());
    }

    public void addClient(Socket socket) {
        clients.add(socket);
    }

    public HashSet<Socket> getClients() {
        return clients;
    }

    public void removeClient(Socket socket) {
        clients.remove(socket);
    }

    public Command receiveCommand(Socket clientSocket) throws IOException, ClassNotFoundException {
        InputStream inputStream = clientSocket.getInputStream();
        byte[] commandSize = new byte[maxMetaData];
        try {
            clientSocket.setSoTimeout(serverWaitingPeriod);
            if (inputStream.read(commandSize) != 0) {
                byte[] command = new byte[ByteBuffer.wrap(commandSize).getInt()];
                if (inputStream.read(command) != 0) {
                    Command currentCommand = (Command) deserialize(command);
                    LOGGER.info("Read command from the client {}: {}", clientSocket.getInetAddress().toString(), currentCommand.toString());
                    return currentCommand;
                }
            }
            return null;
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    public void sendResult(Socket clientSocket, CommandResult result) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        byte[] serializedResult = serialize(result);
        int resultSize = serializedResult.length;

        outputStream.write(ByteBuffer.allocate(maxMetaData).putInt(resultSize).array());
        outputStream.write(serializedResult);
        outputStream.flush();
        LOGGER.info("Sent result to the client {}: {}", clientSocket.getInetAddress().toString(), result.getMessage());
    }

    public Socket acceptConnection() throws IOException {
        try {
            serverSocket.setSoTimeout(serverWaitingPeriod);
            Socket clientSocket = serverSocket.accept();
            LOGGER.info("Established connection with client: {}", clientSocket.getInetAddress().toString());
            return clientSocket;
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

}
