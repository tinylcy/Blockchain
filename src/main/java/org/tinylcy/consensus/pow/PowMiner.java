package org.tinylcy.consensus.pow;

import com.google.common.hash.Hashing;
import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Blockchain;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.ConfigurationUtils;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.HashingUtils;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.network.*;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tinylcy.
 */
public class PowMiner extends Peer {

    private static final Logger LOGGER = Logger.getLogger(PowMiner.class);

    private static Properties configProperties;    // Miner's configuration information.

    private Blockchain blockchain;                 // A blockchain the current miner maintained.

    private Queue<Transaction> transactionPool;    // A transaction pool for transactions to be confirmed.
    private List<Transaction> currTransactions;    // Saved transactions in the current block.

    private List<Peer> peers;                      // The connected peers in the network, now we should set it manually.

    private Multicast multicast;                   // Network module.
    private Peer2Peer peer2Peer;                   // Network module.

    private PowBlockMiner blockMiner;              // The thread for mining block.
    private PowMessageListener msgListener;        // The thread for sending and receiving message (BLOCK, CHAIN_REQUEST and CHAIN_RESPONSE).
    private PowTransactionListener transListener;  // The thread for receiving transactions.

    private Boolean genesisMiner;                  // Mark the Satoshi Nakamoto miner in blockchain network.

    static {
        configProperties = new Properties();
        ConfigurationUtils.loadPeerConfig(configProperties);
    }

    public PowMiner() {
        this(InetAddressUtils.getIP(), Integer.parseInt(configProperties.getProperty("MINER_DEFAULT_TCP_PORT")));
        String ip = getIp();
        if (ip != null && ip.equals(configProperties.getProperty("GENESIS_PEER_IP"))) {
            genesisMiner = true;
        } else {
            genesisMiner = false;
        }
    }

    public PowMiner(String ip, Integer port) {
        super(ip, port);
        this.blockchain = new Blockchain();
        this.transactionPool = new LinkedBlockingQueue<Transaction>();
        this.currTransactions = new ArrayList<Transaction>();

        this.multicast = new Multicast();
        this.peer2Peer = new Peer2Peer();

        this.peers = ConfigurationUtils.peers();

        /* Restart the miner thread and the listener thread */
        blockMiner = new PowBlockMiner(this);           // Create a miner thread.
        msgListener = new PowMessageListener(this);     // Create a listener thread.
        transListener = new PowTransactionListener(this);
    }

    public synchronized Block createBlockWithoutNonce() {
        Block block = new Block();
        Integer length = blockchain.getMainChain().size();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        block.setTimestamp(timestamp.getTime());
        block.setTransactions(fetchTransactionsFromPool());
        block.setPrevBlockHash(HashingUtils.sha256(blockchain.getLastBlock()));
        block.setHeight(length);
        return block;
    }

    public synchronized void appendBlock(Block block, Peer blockSrcPeer) {
        /**
         * Before appending the newly-mined block into blockchain,
         * the mining thread should be stopped at first.
         */
        blockMiner.stopMining();

        /**
         * Try to append the newly-mined block into the blockchain,
         * including main chain and backup chains.
         */
        if (blockchain.appendBlock(block)) {
            blockMiner.startMining();
            return;
        }

        /**
         * If the newly-mined block can not be appended after the current blockchain,
         * multicast a chain-request message to get longer blockchain from other peers.
         */
        syncMainChain(blockSrcPeer);
    }

    public synchronized void replaceMainChain(List<Block> chain) {
        blockchain.replaceMainChain(chain);
        LOGGER.info(InetAddressUtils.getIP() + " - Finished sync main chain.");
    }

    public Boolean validateChain() {
        if (blockchain.getMainChain().size() == 0) {
            return true;
        }

        for (int index = 1; index < blockchain.getMainChain().size(); index++) {
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

    public void syncMainChain(Peer syncPeer) {
        Message syncMsg = new Message(owner(), null, MessageType.CHAIN_REQUEST);
        LOGGER.info(InetAddressUtils.getIP() + " - Sent main chain sync message.");
        peer2Peer.send(FastJsonUtils.getJsonString(syncMsg), syncPeer);
    }

    public void init() {
        blockMiner.start();
        msgListener.start();
        transListener.start();
    }

    public Long proofOfWork(Block block) {
        Long nonce;
        String sha256;

        sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block), StandardCharsets.UTF_8).toString();
        for (nonce = 0L; !isValidChain(sha256); nonce++) {
            if (!blockMiner.isRunning()) {
                LOGGER.info(InetAddressUtils.getIP() + " - Abort mining.");
                return -1L;
            }
            sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block) + nonce, StandardCharsets.UTF_8).toString();
        }

        return nonce;
    }

    private List<Transaction> fetchTransactionsFromPool() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        while (!transactionPool.isEmpty()) {
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
        if (blockMiner != null && blockMiner.isRunning()) {
            blockMiner.stopMining();
        }
    }

    public void startMining() {
        if (blockMiner != null && !blockMiner.isRunning()) {
            blockMiner.startMining();
        }
    }

    public void startMsgListening() {
        if (msgListener != null && !msgListener.isRunning()) {
            msgListener.startListening();
        }
    }

    public void startTransListening() {
        if (transListener != null && !transListener.isRunning()) {
            transListener.startListening();
        }
    }

    public Peer owner() {
        return new Peer(getIp(), getPort());
    }

    public Integer chainSize() {
        return blockchain.getMainChain().size();
    }

    public Boolean isGenesisMiner() {
        return genesisMiner;
    }

    public List<List<Block>> getBackupChains() {
        return blockchain.getBackupChains();
    }

    public Properties getConfigProperties() {
        return configProperties;
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


    public PowMessageListener getMsgListener() {
        return msgListener;
    }

    public void setMsgListener(PowMessageListener msgListener) {
        this.msgListener = msgListener;
    }

    public PowTransactionListener getTransListener() {
        return transListener;
    }

    public void setTransListener(PowTransactionListener transListener) {
        this.transListener = transListener;
    }

    public PowBlockMiner getBlockMiner() {
        return blockMiner;
    }

    public void setBlockMiner(PowBlockMiner blockMiner) {
        this.blockMiner = blockMiner;
    }

    public List<Transaction> getCurrTransactions() {
        return currTransactions;
    }

    public void setCurrTransactions(List<Transaction> currTransactions) {
        this.currTransactions = currTransactions;
    }

    public Peer2Peer getPeer2Peer() {
        return peer2Peer;
    }

    public void setPeer2Peer(Peer2Peer peer2Peer) {
        this.peer2Peer = peer2Peer;
    }

    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }

    public Multicast getMulticast() {
        return multicast;
    }

    public void setMulticast(Multicast multicast) {
        this.multicast = multicast;
    }
}
