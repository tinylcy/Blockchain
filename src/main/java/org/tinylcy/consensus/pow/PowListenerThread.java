package org.tinylcy.consensus.pow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;
import org.tinylcy.network.Multicast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class PowListenerThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PowListenerThread.class);

    private PowMiner miner;
    private Multicast multicast;
    private Boolean isRunning;

    public PowListenerThread(PowMiner miner) {
        this.miner = miner;
        this.multicast = new Multicast();
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            byte[] bytes = multicast.receive();
            if (bytes == null || bytes.length == 0) {
                continue;
            }

            Message msg = FastJsonUtils.parseMessage(bytes);

            if (msg.getType().equals(MessageType.BLOCK) &&
                    msg.getSender() != null && !msg.getSender().getIp().equals(miner.getIp())) {
                Block block = FastJsonUtils.parseObject(msg.getData().toString(), Block.class);
                LOGGER.info("Received a block: " + block);

                /**
                 * When a new block has been mined, maybe this block was mined by the miner itself
                 * or by other peers, the miner should stop mining at first and then append this
                 * newly-mined block into the blockchain.
                 **/
                miner.stopMining();
                miner.appendBlock(block);
                miner.restartMining();   // Start to mining the next block.

            } else if (msg.getType().equals(MessageType.TRANSACTION)) {
                Transaction transaction = FastJsonUtils.parseObject(msg.getData().toString(), Transaction.class);
                miner.addTransactionIntoPool(transaction);
                LOGGER.info("Received a transaction: " + transaction);

            } else if (msg.getType().equals(MessageType.CHAIN_REQUEST) && !msg.getSender().getIp().equals(miner.getIp())) {
                Message response = new Message(miner.owner(), miner.getMainChain(), MessageType.CHAIN_RESPONSE);
                multicast.send(FastJsonUtils.getJsonString(response).getBytes());
                LOGGER.info("Multicast main chain to other peers.");

            } else if (msg.getType().equals(MessageType.CHAIN_RESPONSE) && !msg.getSender().getIp().equals(miner.getIp())) {
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

            } else {
                // TODO
            }

        }
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
