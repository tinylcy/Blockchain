package org.tinylcy.chain;

import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.HashingUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Blockchain {

    private List<Block> chain;
    private List<Transaction> currTransactions;

    public Blockchain() {
        this.chain = new ArrayList<Block>();
        this.currTransactions = new ArrayList<Transaction>();

        /* Initialize genesis block. */
        Block genesis = new Block();
        genesis.setIndex(0L);
        genesis.setPrevHash("0");
        genesis.setNonce(0L);
        this.chain.add(genesis);
    }

    public void acceptTransaction(Transaction transaction) {
        currTransactions.add(transaction);
    }

    public Block createBlockWithoutNonce() {
        Block block = new Block();
        block.setIndex((long) chain.size());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        block.setTimestamp(timestamp.getTime());
        block.setTransactions(currTransactions);
        block.setPrevHash(HashingUtils.sha256(FastJsonUtils.getJsonString(chain.get(chain.size() - 1))));
        return block;
    }

    public void appendBlock(Block block) {
        chain.add(block);
        currTransactions = new ArrayList<Transaction>(); // Empty current transaction list.
    }

    public List<Block> getChain() {
        return chain;
    }

    public void setChain(List<Block> chain) {
        this.chain = chain;
    }

    public List<Transaction> getCurrTransactions() {
        return currTransactions;
    }

    public void setCurrTransactions(List<Transaction> currTransactions) {
        this.currTransactions = currTransactions;
    }
}
