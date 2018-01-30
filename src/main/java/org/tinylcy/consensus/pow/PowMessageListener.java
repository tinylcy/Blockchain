package org.tinylcy.consensus.pow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
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
    private Peer2Peer peer2Peer;
    private volatile Boolean isRunning;

    public PowMessageListener(PowMiner miner) {
        this.miner = miner;
        this.peer2Peer = miner.getPeer2Peer();
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!isRunning) {
                continue;
            }
            Message msg = FastJsonUtils.parseObject(peer2Peer.receive(), Message.class);
            if (msg == null) {
                continue;
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

            } else if (msg.getType().equals(MessageType.CHAIN_REQUEST) && !msg.getSender().getIp().equals(miner.getIp())) {
                Message response = new Message(miner.owner(), miner.getMainChain(), MessageType.CHAIN_RESPONSE);
                LOGGER.info(InetAddressUtils.getIP() + " - Sent a main chain to " + msg.getSender());
                peer2Peer.send(FastJsonUtils.getJsonString(response), msg.getSender());

            } else if (msg.getType().equals(MessageType.CHAIN_RESPONSE) && !msg.getSender().getIp().equals(miner.getIp())) {
                LOGGER.info(InetAddressUtils.getIP() + " - Received a main chain from " + msg.getSender());
                JSONArray array = JSON.parseArray(msg.getData().toString());
                if (array.size() <= miner.chainSize()) {
                    return;
                }
                List<Block> chain = new ArrayList<Block>(array.size());
                for (Object elem : array) {
                    chain.add(FastJsonUtils.parseObject(elem.toString(), Block.class));
                }
                miner.replaceMainChain(chain);  // replace the current main blockchain

                miner.startMining();


            } else {
                // TODO
            }

        }
    }

    public void startListening() {
        isRunning = true;
        LOGGER.info(InetAddressUtils.getIP() + " - The message listening thread started.");
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
