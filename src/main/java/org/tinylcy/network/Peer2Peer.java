package org.tinylcy.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tinylcy.
 */
public class Peer2Peer {

    private ServerSocket serverSocket;

    public Peer2Peer(Integer port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message msg, Peer server) {
        Socket socket = null;
        ObjectOutputStream output = null;
        try {
            socket = new Socket(server.getIp(), server.getPort());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(msg);
        } catch (IOException e) {
            // TODO
            System.err.println("Can not establish a connection with: " + server);
        } finally {
            close(null, output, socket);
        }
    }

    public Message receive() {
        Socket client = null;
        ObjectInputStream input = null;
        Message msg = null;

        try {
            client = serverSocket.accept();
            input = new ObjectInputStream(client.getInputStream());
            msg = (Message) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close(input, null, client);
        }

        return msg;
    }

    private void close(ObjectInputStream input, ObjectOutputStream output, Socket socket) {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
