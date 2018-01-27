package org.tinylcy.consensus.pow;

import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;

/**
 * Created by tinylcy.
 */
public class PowMinerThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PowMinerThread.class);

    private PowMiner miner;
    private volatile Boolean isRunning;

    public PowMinerThread(PowMiner miner) {
        this.miner = miner;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            Block block = miner.createBlockWithoutNonce();
            Long nonce = miner.proofOfWork(block);
            block.setNonce(nonce);

            /**
             * If a new block has been mined, append the block into its own blockchain,
             * and then pack it as a message and multicast the message to peers.
             **/
            Message msg = new Message(null, block, MessageType.BLOCK);
            miner.appendBlock(block);
            miner.getMulticast().send(FastJsonUtils.getJsonString(msg).getBytes());

            LOGGER.info("Multicast a new block: " + msg.getData());
        }
    }

    public void stopMining() {
        isRunning = false;
    }

    public void startMining() {
        isRunning = true;
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
