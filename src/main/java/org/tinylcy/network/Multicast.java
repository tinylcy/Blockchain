package org.tinylcy.network;

import org.apache.log4j.Logger;
import org.tinylcy.config.Constants;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by tinylcy.
 */
public class Multicast {

    private static final Logger LOGGER = Logger.getLogger(Multicast.class);

    private InetAddress group;
    private MulticastSocket sendSocket;
    private MulticastSocket receiveSocket;

    public Multicast() {
        try {
            group = InetAddress.getByName(Constants.MULTICAST_GROUP_ADDRESS);
            sendSocket = new MulticastSocket();
            receiveSocket = new MulticastSocket(Constants.MULTICAST_GROUP_PORT);
            receiveSocket.joinGroup(group);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes) {
        try {
            sendSocket.send(new DatagramPacket(bytes, bytes.length, group, Constants.MULTICAST_GROUP_PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive() {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            receiveSocket.receive(packet);
            byte[] data = Arrays.copyOfRange(buffer, 0, packet.getLength());
            return data;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
