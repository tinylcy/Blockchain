package org.tinylcy.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Block;
import org.tinylcy.config.Constants;
import org.tinylcy.consensus.pow.PowMiner;
import org.tinylcy.network.Peer;

import java.util.List;

/**
 * Created by tinylcy.
 */
@RestController
public class MinerController {

    private static final Logger LOGGER = Logger.getLogger(MinerController.class);

    private static PowMiner miner;

    static {
        miner = new PowMiner();
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public @ResponseBody Boolean mine() {
        miner.init();
        if (!miner.isGenesisMiner()) {
            miner.startListening();
            miner.syncMainChain(new Peer(Constants.GENESIS_PEER_IP, Constants.GENESIS_PEER_PORT));
        }else {
            miner.startListening();
            miner.startMining();
        }
        return true;
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
        miner.stopMining();
    }

}
