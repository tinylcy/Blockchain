package org.tinylcy.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Transaction;

/**
 * Created by tinylcy.
 */
@RestController
public class WalletController {

    private static final Logger LOGGER = Logger.getLogger(WalletController.class);

    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public void newTransaction(@RequestParam("sender") String sender, @RequestParam("recipient") String recipient,
                               @RequestParam("amount") Double amount) {
        Transaction transaction = new Transaction(sender, recipient, amount);

        LOGGER.info("New transaction: " + transaction);
    }
}
