package org.tinylcy.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Blockchain {

    private List<Block> mainChain;
    private List<List<Block>> backupChains;

    public Blockchain() {
        this.mainChain = new ArrayList<Block>();

        /* Initialize the genesis block. */
        Block genesis = new Block();
        genesis.setPrevBlockHash("0");
        genesis.setNonce(0L);
        genesis.setMerkleRoot("0");
        genesis.setHeight(0);
        mainChain.add(genesis);
    }

    public void replaceMainChain(List<Block> chain) {
        mainChain = new ArrayList<Block>(chain.size());
        for(Block block: chain) {
            mainChain.add(block);
        }
    }

    public Block getLastBlock() {
        return mainChain.get(mainChain.size() - 1);
    }

    public List<Block> getMainChain() {
        return mainChain;
    }

    public void setMainChain(List<Block> mainChain) {
        this.mainChain = mainChain;
    }

    public List<List<Block>> getBackupChains() {
        return backupChains;
    }

    public void setBackupChains(List<List<Block>> backupChains) {
        this.backupChains = backupChains;
    }

}
