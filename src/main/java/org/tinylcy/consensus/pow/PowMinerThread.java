package org.tinylcy.consensus.pow;

import org.tinylcy.chain.Block;

/**
 * Created by tinylcy.
 */
public class PowMinerThread extends Thread {

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
            System.out.println("A new block has been appended.");
        }
    }

    public void shutdown() {
        started = false;
    }
}
