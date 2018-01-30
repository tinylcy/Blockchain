package org.tinylcy.chain;

import org.apache.log4j.Logger;
import org.tinylcy.common.HashingUtils;
import org.tinylcy.common.InetAddressUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Blockchain {

    private static final Logger LOGGER = Logger.getLogger(Blockchain.class);

    private List<Block> mainChain;
    private List<List<Block>> backupChains;

    public Blockchain() {
        this.mainChain = new ArrayList<Block>();
        this.backupChains = new ArrayList<List<Block>>();

        /* Initialize the genesis block. */
        Block genesis = new Block();
        genesis.setPrevBlockHash("0");
        genesis.setNonce(0L);
        genesis.setMerkleRoot("0");
        genesis.setHeight(0);
        mainChain.add(genesis);
    }

    /**
     * Append the newly-mined block after/into main chain or backup chains.
     * If appended, return {true}.
     * If no where to append, return {false}.
     *
     * @param block
     * @return
     */
    public Boolean appendBlock(Block block) {
        String sha256 = HashingUtils.sha256(getLastBlock());

        // Try to append after the main chain.
        if (sha256.equals(block.getPrevBlockHash())) {
            mainChain.add(block);
            return true;
        }

        // Try to append after one of the backup chain.
        if (appendAfterBackupChain(block)) {
            return true;
        }

        // Try to append into the main chain, which will create a backup chain.
        if (appendIntoMainChain(block)) {
            return true;
        }

        // Remove the out of date chain.
        Iterator<List<Block>> iterator = backupChains.iterator();
        while (iterator.hasNext()) {
            List<Block> backupChain = iterator.next();
            if (mainChain.size() - backupChain.size() > 5) {
                iterator.remove();
            }
        }

        return false;
    }

    private Boolean appendAfterBackupChain(Block block) {
        for (List<Block> backupChain : backupChains) {
            Block lastBlock = backupChain.get(backupChain.size() - 1);
            String sha256 = HashingUtils.sha256(lastBlock);
            if (sha256.equals(block.getPrevBlockHash())) {
                backupChain.add(block);
                if (backupChain.size() > mainChain.size()) {
                    List<Block> tmpChain = mainChain;
                    mainChain = backupChain;
                    backupChain = tmpChain;
                    LOGGER.info(InetAddressUtils.getIP() + " - Swap main chain and backup chain.");
                }
                return true;
            }
        }
        return false;
    }

    private Boolean appendIntoMainChain(Block block) {
        Integer length = mainChain.size();
        for (int i = length - 2; i > 0; i--) {
            Block prevBlock = mainChain.get(i);
            String sha256 = HashingUtils.sha256(prevBlock);
            if (sha256.equals(block.getPrevBlockHash())) {
                List<Block> backupChain = createBackupChain(prevBlock);
                backupChain.add(block);
                // TODO: remove
                if (backupChain.size() > mainChain.size()) {
                    List<Block> tmpChain = backupChain;
                    backupChain = mainChain;
                    mainChain = tmpChain;
                    LOGGER.info(InetAddressUtils.getIP() + " - Swap main chain and backup chain.");
                }
                backupChains.add(backupChain);
                return true;
            }
        }
        return false;
    }

    private List<Block> createBackupChain(Block prevBlock) {
        List<Block> backupChain = new ArrayList<Block>();
        for (Block block : mainChain) {
            if (prevBlock == block) {
                break;
            }
            backupChain.add(block);
        }
        backupChain.add(prevBlock);
        return backupChain;
    }

    public void replaceMainChain(List<Block> chain) {
        mainChain = new ArrayList<Block>(chain.size());
        for (Block block : chain) {
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
