package org.tinylcy.consensus.pow;

import org.apache.log4j.Logger;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;

/**
 * Created by chenyang li.
 */
public class PowTransactionListener extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PowTransactionListener.class);

    private PowMiner miner;
    private volatile Boolean isRunning;

    public PowTransactionListener(PowMiner miner) {
        this.miner = miner;
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!isRunning) {
                continue;
            }

            Message msg = FastJsonUtils.parseObject(miner.getMulticast().receive(), Message.class);
            if (!msg.getType().equals(MessageType.TRANSACTION)) {
                continue;
            }

            Transaction transaction = FastJsonUtils.parseObject(msg.getData().toString(), Transaction.class);
            if (checkTransaction(transaction)) {
                miner.addTransactionIntoPool(transaction);
                LOGGER.info(InetAddressUtils.getIP() + " - Received a valid transaction: " + transaction);
            } else {
                LOGGER.info(InetAddressUtils.getIP() + " - Received an invalid transaction: " + transaction);
            }
        }
    }

    private Boolean checkTransaction(Transaction transaction) {
        // TODO
        return true;
    }

    public void startListening() {
        isRunning = true;
        LOGGER.info(InetAddressUtils.getIP() + " - The transaction listening thread started.");
    }

    public Boolean isRunning() {
        return isRunning;
    }

}
