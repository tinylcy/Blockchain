package org.tinylcy.network;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.tinylcy.common.ConfigurationUtils;
import org.tinylcy.common.InetAddressUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by tinylcy.
 */
public class Peer2Peer {

    private static final Logger LOGGER = Logger.getLogger(Peer2Peer.class);
    private static Properties configProperties;

    static {
        configProperties = new Properties();
        ConfigurationUtils.loadPeerConfig(configProperties);
    }

    private ServerSocket serverSocket;

    public Peer2Peer() {
        try {
            Integer port = Integer.parseInt(configProperties.getProperty("MINER_DEFAULT_TCP_PORT"));
            serverSocket = new ServerSocket(port);
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
