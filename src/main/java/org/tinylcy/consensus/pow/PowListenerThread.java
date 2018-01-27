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

            if (msg.getType().equals(MessageType.BLOCK) && !msg.getSender().equals(miner.getOwner())) {
                Block block = (Block) msg.getData();
                LOGGER.info("Received a block: " + block);
            } else if (msg.getType().equals(MessageType.TRANSACTION)){
                Transaction transaction = (Transaction) msg.getData();
                miner.addTransactionIntoPool(transaction);
                LOGGER.info("Received a transaction: " + transaction);
            } else {
                LOGGER.warn("Received an invalid message: " + msg);
            }
        }
    }


}
