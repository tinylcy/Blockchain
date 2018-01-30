package org.tinylcy.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by tinylcy.
 */
public class MulticastPublisher {

    public void multicast(String msg) throws Exception {
        DatagramSocket socket;
        InetAddress group;
        byte[] buf;
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.4");
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 9999);
        socket.send(packet);
        System.err.println("Sent message: " + msg);
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        new MulticastPublisher().multicast("tinylcy");
    }
}
