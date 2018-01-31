package org.tinylcy.network;

import org.apache.log4j.Logger;
import org.tinylcy.common.ConfigurationUtils;
import org.tinylcy.common.InetAddressUtils;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by tinylcy.
 */
public class Peer implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Peer.class);
    private static Properties configProperties;

    static {
        configProperties = new Properties();
        ConfigurationUtils.loadPeerConfig(configProperties);
    }

    private String ip;
    private Integer port;

    public Peer() {
    }

    public Peer(String ip) {
        this.ip = ip;
        this.port = Integer.parseInt(configProperties.getProperty("MINER_DEFAULT_TCP_PORT"));
    }

    public Peer(Integer port) {
        this.ip = InetAddressUtils.getIP();
        this.port = port;
    }

    public Peer(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
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

    @Override
    public String toString() {
        return "Peer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
