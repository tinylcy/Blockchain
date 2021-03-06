package org.tinylcy.network;

import org.apache.log4j.Logger;
import org.tinylcy.common.ConfigurationUtils;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by tinylcy.
 */
public class Multicast {

    private static final Logger LOGGER = Logger.getLogger(Multicast.class);
    private static Properties configProperties;

    static {
        configProperties = new Properties();
        ConfigurationUtils.loadPeerConfig(configProperties);
    }

    private InetAddress group;
    private MulticastSocket sendSocket;
    private MulticastSocket receiveSocket;

    public Multicast() {
        try {
            group = InetAddress.getByName(configProperties.getProperty("MULTICAST_GROUP_ADDRESS"));
            sendSocket = new MulticastSocket();
            receiveSocket = new MulticastSocket(Integer.parseInt(configProperties.getProperty("MULTICAST_GROUP_PORT")));
            receiveSocket.joinGroup(group);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        try {
            byte[] bytes = data.getBytes();
            Integer port = Integer.parseInt(configProperties.getProperty("MULTICAST_GROUP_PORT"));
            sendSocket.send(new DatagramPacket(bytes, bytes.length, group, port));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            receiveSocket.receive(packet);
            byte[] data = Arrays.copyOfRange(buffer, 0, packet.getLength());
            String result = new String(data);
            return result;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
