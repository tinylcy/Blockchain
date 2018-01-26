package org.tinylcy.network;

import org.apache.log4j.Logger;
import org.tinylcy.config.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by tinylcy.
 */
public class Multicast {

    private static final Logger LOGGER = Logger.getLogger(Multicast.class);

    public void send(byte[] bytes) {
        InetAddress group;
        MulticastSocket socket = null;

        try {
            group = InetAddress.getByName(Constants.MULTICAST_GROUP_ADDRESS);
            socket = new MulticastSocket();
            socket.joinGroup(group);
            socket.send(new DatagramPacket(bytes, bytes.length, group, Constants.MULTICAST_GROUP_PORT));
            LOGGER.info("Sent data: " + new String(bytes, StandardCharsets.UTF_8));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != socket) {
                socket.close();
            }
        }
    }

    public byte[] receive() {
        InetAddress group;
        MulticastSocket socket = null;

        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            group = InetAddress.getByName(Constants.MULTICAST_GROUP_ADDRESS);
            socket = new MulticastSocket(Constants.MULTICAST_GROUP_PORT);
            socket.joinGroup(group);

            socket.receive(packet);

            if (packet.getLength() > 1024) {
                throw new RuntimeException("The data should not be larger then 1M.");
            }
            byte[] data = Arrays.copyOfRange(buffer, 0, packet.getLength());
            LOGGER.info("Received data: " + new String(data, StandardCharsets.UTF_8));
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != socket) {
                socket.close();
            }
        }

        return null;
    }

}
