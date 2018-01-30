package org.tinylcy.consensus.pow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;
import org.tinylcy.network.Peer2Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class PowMessageListener extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PowMessageListener.class);

    private PowMiner miner;
//    private Multicast multicast;
    private Peer2Peer peer2Peer;
    private volatile Boolean isRunning;

    public PowMessageListener(PowMiner miner) {
        this.miner = miner;
//        this.multicast = new Multicast();
        this.peer2Peer = miner.getPeer2Peer();
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!isRunning) {
                continue;
            }
            System.err.println("------------------- The listener thread is running... ---------------------");
//            byte[] bytes = multicast.receive();
            Message msg = FastJsonUtils.parseObject(peer2Peer.receive(), Message.class);
            if (msg == null) {
                continue;
            }

//            Message msg = FastJsonUtils.parseMessage(bytes);

            if (!msg.getSender().getIp().equals(miner.getIp())) {
                System.err.println("sender: " + msg.getSender().getIp() + ", receiver: " + miner.getIp() + ", msg type: " + msg.getType());
            }

            if (msg.getType().equals(MessageType.BLOCK) &&
                    msg.getSender() != null && !msg.getSender().getIp().equals(miner.getIp())) {

                Block block = FastJsonUtils.parseObject(msg.getData().toString(), Block.class);
                LOGGER.info(InetAddressUtils.getIP() + " - Received a block: " + block);

                /**
                 * When a new block has been mined, maybe this block was mined by the miner itself
                 * or by other peers, the miner should stop mining at first and then append this
                 * newly-mined block into the blockchain.
                 **/
                miner.appendBlock(block, msg.getSender());

            } else if (msg.getType().equals(MessageType.TRANSACTION)) {
                Transaction transaction = FastJsonUtils.parseObject(msg.getData().toString(), Transaction.class);
                miner.addTransactionIntoPool(transaction);
                LOGGER.info(InetAddressUtils.getIP() + " - Received a transaction: " + transaction);

            } else if (msg.getType().equals(MessageType.CHAIN_REQUEST) && !msg.getSender().getIp().equals(miner.getIp())) {
                Message response = new Message(miner.owner(), miner.getMainChain(), MessageType.CHAIN_RESPONSE);
//                multicast.send(FastJsonUtils.getJsonString(response).getBytes());

                peer2Peer.send(FastJsonUtils.getJsonString(response), msg.getSender());
                LOGGER.info(InetAddressUtils.getIP() + " - Multicast main chain to other peers.");

            } else if (msg.getType().equals(MessageType.CHAIN_RESPONSE) && !msg.getSender().getIp().equals(miner.getIp())) {
                //miner.stopMining();
                //System.err.println(InetAddressUtils.getIP() + " - Receive chain response and stop mining.");

                JSONArray array = JSON.parseArray(msg.getData().toString());
                if (array.size() <= miner.chainSize()) {
                    return;
                }
                List<Block> chain = new ArrayList<Block>(array.size());
                for (Object elem : array) {
                    chain.add(FastJsonUtils.parseObject(elem.toString(), Block.class));
                }
                miner.replaceMainChain(chain);
                // TODO replace the current main blockchain

                miner.startMining();


            } else {
                // TODO
            }

        }
    }

    public void startListening() {
        isRunning = true;
        LOGGER.info("The message listening thread started.");
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
