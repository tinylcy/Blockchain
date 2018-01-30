package org.tinylcy.config;

import org.tinylcy.network.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Constants {

    public static final String GENESIS_PEER_IP = "192.168.0.121";
    public static final Integer GENESIS_PEER_PORT = 9999;

    public static final Integer MINER_DEFAULT_TCP_PORT = 9999;

    public static final String MULTICAST_GROUP_ADDRESS = "224.0.0.3";
    public static final Integer MULTICAST_GROUP_PORT = 9876;

    public static final Integer MAX_TRANSACTION_NUM_PER_BLOCK = 4000;

    public static List<Peer> mockPeers() {
        List<Peer> peers = new ArrayList<Peer>();
        peers.add(new Peer("192.168.0.121", 9999));
        peers.add(new Peer("192.168.0.120", 8888));
        return peers;
    }

}
