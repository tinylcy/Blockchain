package org.tinylcy.consensus.pow;

import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.common.FastJsonUtils;

/**
 * Created by tinylcy.
 */
public class PowMinerThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PowMinerThread.class);

    private PowMiner miner;
    private volatile Boolean started;

    public PowMinerThread(PowMiner miner) {
        this.miner = miner;
        this.started = true;
    }

    @Override
    public void run() {
        while (started) {
            Block block = miner.createBlockWithoutNonce();
            Long nonce = miner.proofOfWork(block);
            block.setNonce(nonce);
            miner.appendBlock(block);

            /* Multicast the block to other peers. */
            byte [] bytes = FastJsonUtils.getJsonString(block).getBytes();
            miner.getMulticast().send(bytes);

            LOGGER.info("A new block has been appended.");
        }
    }

    public void shutdown() {
        started = false;
    }
}
