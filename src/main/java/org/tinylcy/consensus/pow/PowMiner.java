package org.tinylcy.consensus.pow;

import com.google.common.hash.Hashing;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Blockchain;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.HashingUtils;
import org.tinylcy.network.Peer;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class PowMiner extends Peer {

    private Blockchain blockchain;
    private List<Transaction> transactions;
    private List<Transaction> unused;

    private PowMinerThread minerThread;


    public PowMiner(String ip, Integer port) {
        super(ip, port);
        this.blockchain = new Blockchain();
        this.transactions = new ArrayList<Transaction>();
        this.unused = new ArrayList<Transaction>();
    }

    public void acceptTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public Block createBlockWithoutNonce() {
        Block block = new Block();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        block.setTimestamp(timestamp.getTime());
        block.setTransactions(transactions);
        block.setPrevBlockHash(HashingUtils.sha256(
                FastJsonUtils.getJsonString(
                        blockchain.getMainChain().get(blockchain.getMainChain().size() - 1))));
        return block;
    }

    public void appendBlock(Block block) {
        Integer length = blockchain.getMainChain().size();
        // TODO
        blockchain.getMainChain().add(block);
        transactions = new ArrayList<Transaction>(); // Empty current transaction list.
    }

    public void registerNeighbour(Peer neighbour) {

    }

    public Boolean validateChain() {
        if (blockchain.getMainChain().size() == 0) {
            return true;
        }
        Integer index;
        for (index = 1; index < blockchain.getMainChain().size(); index++) {
            String prevHash = HashingUtils.sha256(FastJsonUtils.getJsonString(blockchain.getMainChain().get(index - 1)));
            if (!prevHash.equals(blockchain.getMainChain().get(index).getPrevBlockHash())) {
                return false;
            }
        }
        return true;
    }

    public void resolveConflicts() {
        Integer maxChainLen = blockchain.getMainChain().size();

    }

    public void mine() {
        minerThread = new PowMinerThread(this);
        minerThread.start();
        System.out.println("Miner thread started...");
    }

    public Long proofOfWork(Block block) {
        Long nonce;
        String sha256;

        sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block), StandardCharsets.UTF_8).toString();
        for (nonce = 0L; !isValidChain(sha256); nonce++) {
            sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block) + nonce, StandardCharsets.UTF_8).toString();
            // System.out.println("sha256: " + sha256 + ", nonce: " + nonce);
        }
        return nonce;
    }

    private Boolean isValidChain(String proof) {
        return proof.startsWith("00000");
    }

    public List<Block> getMainChain() {
        return blockchain.getMainChain();
    }

    public void shutdown() {
        minerThread.shutdown();
    }

}
