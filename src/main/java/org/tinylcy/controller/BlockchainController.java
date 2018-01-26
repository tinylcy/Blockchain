package org.tinylcy.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Transaction;
import org.tinylcy.consensus.pow.PowMiner;

import java.util.List;

/**
 * Created by tinylcy.
 */
@RestController
public class BlockchainController {

    private static final Logger LOGGER = Logger.getLogger(BlockchainController.class);

    private static PowMiner miner;

    static {
        miner = new PowMiner("127.0.0.1", 8080);
    }


    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public void mine() {
        miner.mine();
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public void newTransaction(@RequestParam("sender") String sender, @RequestParam("recipient") String recipient,
                               @RequestParam("amount") Double amount) {
        Transaction transaction = new Transaction(sender, recipient, amount);
        if (null == miner) {
            throw new RuntimeException("The blockchain has not started yet, please start it first.");
        }
        miner.acceptTransaction(transaction);
        LOGGER.info("New transaction: " + transaction);
    }

    @RequestMapping(value = "/chain", method = RequestMethod.GET)
    public List<Block> chain() {
        return miner.getMainChain();
    }

    @RequestMapping(value = "/valid", method = RequestMethod.GET)
    public Boolean validate() {
        return miner.validateChain();
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    public void shutdown() {
        miner.shutdown();
    }

}
