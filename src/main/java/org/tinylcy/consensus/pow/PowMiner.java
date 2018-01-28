package org.tinylcy.consensus.pow;

import com.google.common.hash.Hashing;
import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Blockchain;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.HashingUtils;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.config.Constants;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;
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
public class PowMiner extends Peer {

    private static final Logger LOGGER = Logger.getLogger(PowMiner.class);

    private Blockchain blockchain;                // A blockchain the current miner maintained.

    private Queue<Transaction> transactionPool;   // A transaction pool for transactions to be confirmed.
    private List<Transaction> currTransactions;   // Saved transactions in the current block.

    private Multicast multicast;

    private PowMinerThread minerThread;           // The thread for mining.
    private PowListenerThread listenerThread;     // The thread for sending and receiving message in network.

    public PowMiner() {
        this(Constants.OWNER_DEFAULT_NAME);
    }

    public PowMiner(String name) {
        super(InetAddressUtils.getIP(), name);
        this.blockchain = new Blockchain();
        this.transactionPool = new LinkedBlockingQueue<Transaction>();
        this.currTransactions = new ArrayList<Transaction>();

        this.multicast = new Multicast();
    }

    public Block createBlockWithoutNonce() {
        Block block = new Block();
        Integer length = blockchain.getMainChain().size();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        block.setTimestamp(timestamp.getTime());
        block.setTransactions(fetchTransactionsFromPool());
        block.setPrevBlockHash(HashingUtils.sha256(blockchain.getLastBlock()));
        block.setHeight(length);
        return block;
    }

    public synchronized void appendBlock(Block block) {
        Integer length = blockchain.getMainChain().size();
        String sha265 = HashingUtils.sha256(blockchain.getLastBlock());

        /**
         * Try to append the newly-mined block into the main blockchain.
         */
        if (sha265.equals(block.getPrevBlockHash())) {
            blockchain.getMainChain().add(block);
            currTransactions = new ArrayList<Transaction>(); // Empty current transaction list.
            LOGGER.info("A new block have been appended after the main blockchain.");
            return;
        }

        /**
         * If the newly-mined block can not be appended after the current blockchain,
         * multicast a chain-request message to get longer blockchain from other peers.
         */
        Message chainRequestMsg = new Message(owner(), null, MessageType.CHAIN_REQUEST);
        multicast.send(FastJsonUtils.getJsonString(chainRequestMsg).getBytes());
        LOGGER.info("Sent blockchain request message.");
    }

    public synchronized void replaceMainChain(List<Block> chain) {
        blockchain.setMainChain(chain);
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

    public Boolean mine() {
        if (minerThread != null && minerThread.isRunning() && listenerThread != null && listenerThread.isRunning()) {
            return true;
        }

        /* Restart the miner thread and the listener thread */
        minerThread = new PowMinerThread(this);           // Create a miner thread.
        listenerThread = new PowListenerThread(this);     // Create a listener thread.
        minerThread.start();
        listenerThread.start();
        LOGGER.info("Miner thread and listener thread started.");
        return true;
    }

    public Long proofOfWork(Block block) {
        Long nonce;
        String sha256;

        sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block), StandardCharsets.UTF_8).toString();
        for (nonce = 0L; !isValidChain(sha256); nonce++) {
            sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block) + nonce, StandardCharsets.UTF_8).toString();
        }

        return nonce;
    }

    private List<Transaction> fetchTransactionsFromPool() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        int count = 0;
        while (!transactionPool.isEmpty() && count < Constants.MAX_TRANSACTION_NUM_PER_BLOCK) {
            Transaction transaction = transactionPool.peek();
            transactionPool.remove();
            transactions.add(transaction);
        }
        return transactions;
    }

    private Boolean isValidChain(String proof) {
        return proof.startsWith("000000");
    }

    public List<Block> getMainChain() {
        return blockchain.getMainChain();
    }

    public void stopMining() {
        minerThread.stopMining();
    }

    public void restartMining() {
        minerThread.startMining();
    }

    public Peer owner() {
        return new Peer(getIp(), getName());
    }

    public Integer chainSize() {
        return blockchain.getMainChain().size();
    }

    /*******************************************
     * get/set
     *******************************************/

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
