package org.tinylcy.consensus.pow;

import org.junit.Test;
import org.tinylcy.chain.Block;

import java.util.Date;

/**
 * Created by tinylcy.
 */
public class ProofOfWorkTest {

    @Test
    public void testProofOfWork() {
        Block block = new Block();
        block.setTimestamp(new Date().getTime());
        PowMiner miner = new PowMiner("tinylcy");

        Long nonce = miner.proofOfWork(block);
        System.out.println("Nonce = " + nonce);
    }
}
