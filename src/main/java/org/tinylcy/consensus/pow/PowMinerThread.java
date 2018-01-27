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
            miner.appendBlock(block);

            /* Multicast the block to other peers. */
            byte [] bytes = FastJsonUtils.getJsonString(block).getBytes();

            /* If a new block has been mined, pack it as a message and multicast the message to peers. */
            Message msg = new Message(null, bytes, MessageType.BLOCK);
            miner.getMulticast().send(FastJsonUtils.getJsonString(msg).getBytes());

            LOGGER.info("Multicast a new block: " + msg.getData());
        }
    }

    public void shutdown() {
        isRunning = false;
    }
}
