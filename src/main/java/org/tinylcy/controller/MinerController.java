package org.tinylcy.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Block;
import org.tinylcy.consensus.pow.PowMiner;

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
        return miner.mine();
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
