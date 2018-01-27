package org.tinylcy.network;

/**
 * Created by tinylcy.
 */
public class Peer {

    private String ip;
    private String name;

    public Peer() {
    }

    public Peer(String ip) {
        this.ip = ip;
    }

    public Peer(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
