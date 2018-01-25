package org.tinylcy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Block;
import org.tinylcy.chain.Blockchain;
import org.tinylcy.chain.Transaction;
import org.tinylcy.consensus.pow.Pow;

import java.util.List;

/**
 * Created by tinylcy.
 */
@RestController
public class BlockchainController {

    private Blockchain chain;
    private Pow pow;

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public void start() {
        chain = new Blockchain();
        pow = new Pow();
        System.out.println("Blockchain started.");
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public void mine() {
        while(true) {
            Block block = chain.createBlockWithoutNonce();
            Long nonce = pow.mine(block);
            block.setNonce(nonce);
            chain.appendBlock(block);
            System.out.println("A new block has been appended.");
        }
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public void newTransaction(@RequestParam("sender") String sender, @RequestParam("recipient") String recipient,
                               @RequestParam("amount") Double amount) {
        Transaction transaction = new Transaction(sender, recipient, amount);
        if (null == chain) {
            throw new RuntimeException("The blockchain has not started yet, please start it first.");
        }
        chain.acceptTransaction(transaction);
        System.out.println("New transaction: " + transaction);
    }

    @RequestMapping(value = "/chain", method = RequestMethod.GET)
    public List<Block> chain() {
        return chain.getChain();
    }
}
