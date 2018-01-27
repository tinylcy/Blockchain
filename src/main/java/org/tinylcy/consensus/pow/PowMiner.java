package org.tinylcy.consensus.pow;

import com.google.common.hash.Hashing;
import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Blockchain;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.HashingUtils;
import org.tinylcy.config.Constants;
import org.tinylcy.network.Multicast;
import org.tinylcy.network.Peer;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tinylcy.
 */
public class PowMiner {

    private static final Logger LOGGER = Logger.getLogger(PowMiner.class);

    private Peer owner;                         // Owner.

    private Blockchain blockchain;                // A blockchain the current miner maintained.

    private Queue<Transaction> transactionPool;   // A transaction pool for transactions to be confirmed.
    private List<Transaction> currTransactions;   // Saved transactions in the current block.

    private Multicast multicast;

    private PowMinerThread minerThread;           // The thread for mining.
    private PowListenerThread listenerThread;     // The thread for sending and receiving message in network.

    public PowMiner(String name) {
        this.owner = new Peer(Constants.OWNER_DEFAULT_IP, Constants.MULTICAST_GROUP_PORT);
        this.owner.setName(name);
    }

    public PowMiner(String ip, Integer port, String name) {
        this.owner = new Peer(ip, port, name);
    }

    public PowMiner(String ip, Integer port) {
        this.owner = new Peer(ip, port);
        this.blockchain = new Blockchain();
        this.transactionPool = new LinkedBlockingQueue<Transaction>();
        this.currTransactions = new ArrayList<Transaction>();

        this.multicast = new Multicast();
    }

    public Block createBlockWithoutNonce() {
        Block block = new Block();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        block.setTimestamp(timestamp.getTime());
        block.setTransactions(currTransactions);
        block.setPrevBlockHash(HashingUtils.sha256(
                FastJsonUtils.getJsonString(
                        blockchain.getMainChain().get(blockchain.getMainChain().size() - 1))));
        return block;
    }

    public void appendBlock(Block block) {
        Integer length = blockchain.getMainChain().size();
        // TODO
        blockchain.getMainChain().add(block);
        currTransactions = new ArrayList<Transaction>(); // Empty current transaction list.
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

    public void addTransactionIntoPool(Transaction transaction) {
        transactionPool.add(transaction);
    }

    public void resolveConflicts() {
        Integer maxChainLen = blockchain.getMainChain().size();

    }

    public void mine() {
        minerThread = new PowMinerThread(this);           // Create a miner thread.
        listenerThread = new PowListenerThread(this);     // Create a listener thread.
        minerThread.start();
        listenerThread.start();
        LOGGER.info("Miner thread and listener thread started.");
    }

    public Long proofOfWork(Block block) {
        Long nonce;
        String sha256;

        sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block), StandardCharsets.UTF_8).toString();
        for (nonce = 0L; !isValidChain(sha256); nonce++) {
            sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block) + nonce, StandardCharsets.UTF_8).toString();
            sha256 = Hashing.sha256().hashString(sha256, StandardCharsets.UTF_8).toString();
        }

        return nonce;
    }

    private Boolean isValidChain(String proof) {
        return proof.startsWith("000000");
    }

    public List<Block> getMainChain() {
        return blockchain.getMainChain();
    }

    public void shutdown() {
        minerThread.shutdown();
    }

    /*************
     * get/set
     ****************/

    public Peer getOwner() {
        return owner;
    }

    public void setOwner(Peer owner) {
        this.owner = owner;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public Queue<Transaction> getTransactionPool() {
        return transactionPool;
    }

    public void setTransactionPool(Queue<Transaction> transactionPool) {
        this.transactionPool = transactionPool;
    }

    public PowMinerThread getMinerThread() {
        return minerThread;
    }

    public void setMinerThread(PowMinerThread minerThread) {
        this.minerThread = minerThread;
    }

    public PowListenerThread getListenerThread() {
        return listenerThread;
    }

    public void setListenerThread(PowListenerThread listenerThread) {
        this.listenerThread = listenerThread;
    }

    public List<Transaction> getCurrTransactions() {
        return currTransactions;
    }

    public void setCurrTransactions(List<Transaction> currTransactions) {
        this.currTransactions = currTransactions;
    }

    public Multicast getMulticast() {
        return multicast;
    }

    public void setMulticast(Multicast multicast) {
        this.multicast = multicast;
    }
}
