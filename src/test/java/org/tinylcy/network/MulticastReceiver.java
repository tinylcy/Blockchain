package org.tinylcy.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by tinylcy.
 */
public class MulticastReceiver {

    public void receive() throws Exception {
        MulticastSocket socket = new MulticastSocket(9999);
        InetAddress group = InetAddress.getByName("230.0.0.4");
        socket.joinGroup(group);
        byte[] buf = new byte[256];
        System.err.println("Start receiving message...");
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            if ("end".equals(received)) {
                break;
            }
            System.err.println("Received message: " + received);
        }

        System.err.println("Stop receiving message...");
        socket.leaveGroup(group);
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        new MulticastReceiver().receive();
    }
}
