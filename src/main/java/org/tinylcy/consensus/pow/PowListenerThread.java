package org.tinylcy.consensus.pow;

import org.apache.log4j.Logger;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;
import org.tinylcy.network.Multicast;

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
            } else {
                // LOGGER.warn("Received an invalid message: " + msg);
            }
        }
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
