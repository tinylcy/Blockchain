package org.tinylcy.config;

import org.tinylcy.network.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Constants {

    public static final String GENESIS_PEER_IP = "192.168.0.108";

    public static final Integer MINER_DEFAULT_TCP_PORT = 9999;

    public static final String MULTICAST_GROUP_ADDRESS = "224.0.0.3";
    public static final Integer MULTICAST_GROUP_PORT = 9998;

    public static List<Peer> mockPeers() {
        List<Peer> peers = new ArrayList<Peer>();
        peers.add(new Peer("192.168.0.124"));
        peers.add(new Peer("192.168.0.108"));
        return peers;
    }

}
