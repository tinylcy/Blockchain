package org.tinylcy.consensus.pow;

import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;
import org.tinylcy.network.Peer;

/**
 * Created by tinylcy.
 */
public class PowBlockMiner extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PowBlockMiner.class);

    private PowMiner miner;
    private volatile Boolean isRunning;

    public PowBlockMiner(PowMiner miner) {
        this.miner = miner;
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!isRunning) {
                continue;
            }
            Block block = miner.createBlockWithoutNonce();
            Long nonce = miner.proofOfWork(block);
            if (nonce == -1L || !isRunning) {
                continue;
            }
            block.setNonce(nonce);

            LOGGER.info(InetAddressUtils.getIP() + " has mined and multicast a new block: " + block);

            /**
             * If a new block has been mined, append the block into its own blockchain,
             * and then pack it as a message and multicast the message to peers.
             **/
            Message msg = new Message(miner.owner(), block, MessageType.BLOCK);
            miner.appendBlock(block, miner.owner());
            for (Peer peer : miner.getPeers()) {
                if (peer.getIp().equals(InetAddressUtils.getIP())) {
                    continue;
                }
                miner.getPeer2Peer().send(FastJsonUtils.getJsonString(msg), peer);
            }
        }
    }

    public void stopMining() {
        isRunning = false;
        LOGGER.info(InetAddressUtils.getIP() + " - The block mining thread stopped.");
    }

    public void startMining() {
        isRunning = true;
        LOGGER.info(InetAddressUtils.getIP() + " - The block mining thread started.");
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
