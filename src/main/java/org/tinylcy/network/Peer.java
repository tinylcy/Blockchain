package org.tinylcy.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tinylcy.
 */
public class Peer {

    private String ip;
    private Integer port;

    private Set<Peer> peers;

    public Peer(String ip, Integer port ) {
        this.ip = ip;
        this.port = port;
        this.peers = new HashSet<Peer>();
    }

    private void sendMessage(Peer peer, Object msg) {
        Socket socket;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        try {
            socket = new Socket(peer.getIp(), peer.getPort());
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Set<Peer> getPeers() {
        return peers;
    }

    public void setPeers(Set<Peer> peers) {
        this.peers = peers;
    }
}
