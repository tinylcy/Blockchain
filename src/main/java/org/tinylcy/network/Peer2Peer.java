package org.tinylcy.network;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.config.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tinylcy.
 */
public class Peer2Peer {

    private static final Logger LOGGER = Logger.getLogger(Peer2Peer.class);

    private ServerSocket serverSocket;

    public Peer2Peer() {
        try {
            serverSocket = new ServerSocket(Constants.MINER_DEFAULT_TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg, Peer server) {
        Socket socket = null;
        ObjectOutputStream output = null;
        try {
            socket = new Socket(server.getIp(), server.getPort());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(msg);
        } catch (IOException e) {
            LOGGER.warn(InetAddressUtils.getIP() + " - Can not establish a connection with: " + server);
        } finally {
            IOUtils.closeQuietly(socket);
            IOUtils.closeQuietly(output);
        }
    }

    public String receive() {
        Socket client = null;
        ObjectInputStream input = null;
        String msg = null;

        try {
            client = serverSocket.accept();
            input = new ObjectInputStream(client.getInputStream());
            msg = (String) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(input);
        }

        return msg;
    }

}
