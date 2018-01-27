package org.tinylcy.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Transaction;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.InetAddressUtils;
import org.tinylcy.config.Constants;
import org.tinylcy.network.Message;
import org.tinylcy.network.MessageType;
import org.tinylcy.network.Multicast;
import org.tinylcy.network.Peer;

/**
 * Created by tinylcy.
 */
@RestController
public class WalletController {

    private static final Logger LOGGER = Logger.getLogger(WalletController.class);

    private static Multicast multicast;

    static {
        multicast = new Multicast();
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public void newTransaction(@RequestParam("sender") String sender, @RequestParam("recipient") String recipient,
                               @RequestParam("amount") Double amount) {

        Transaction transaction = new Transaction(sender, recipient, amount);
        Peer owner = new Peer(InetAddressUtils.getIP(), Constants.OWNER_DEFAULT_NAME);
        Message msg = new Message(owner, transaction, MessageType.TRANSACTION);  // Construct a transaction type message.
        multicast.send(FastJsonUtils.getJsonString(msg).getBytes());

        LOGGER.info("Multicast transaction: " + transaction);
    }

    public static Multicast getMulticast() {
        return multicast;
    }

    public static void setMulticast(Multicast multicast) {
        WalletController.multicast = multicast;
    }
}
