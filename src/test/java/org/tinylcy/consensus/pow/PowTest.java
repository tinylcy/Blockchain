package org.tinylcy.consensus.pow;

import org.junit.Test;
import org.tinylcy.chain.Block;

/**
 * Created by tinylcy.
 */
public class PowTest {

    @Test
    public void testProofOfWork() {
        Block block = new Block();
        Pow pow = new Pow();
        Long nonce = pow.mine(block);
        System.out.println(nonce);
    }
}
