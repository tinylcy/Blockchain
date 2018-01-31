package org.tinylcy.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tinylcy.chain.Block;
import org.tinylcy.common.HashingUtils;
import org.tinylcy.consensus.pow.PowMiner;
import org.tinylcy.network.Peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by tinylcy.
 */
@RestController
public class MinerController {

    private static final Logger LOGGER = Logger.getLogger(MinerController.class);

    private static PowMiner miner;

    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public
    @ResponseBody
    Boolean mine() {
        miner = new PowMiner();
        miner.init();
        if (miner.isGenesisMiner()) {
            miner.startMsgListening();
            miner.startTransListening();
            miner.startMining();
        } else {
            miner.startMsgListening();
            miner.startTransListening();
            Properties config = miner.getConfigProperties();
            miner.syncMainChain(new Peer(config.getProperty("GENESIS_PEER_IP"),Integer.parseInt(config.getProperty("MINER_DEFAULT_TCP_PORT"))));
        }
        return true;
    }

    @RequestMapping(value = "/chain", method = RequestMethod.GET)
    public List<BlockWrapper> chain() {
        List<BlockWrapper> list = new ArrayList<BlockWrapper>();
        for (Block block : miner.getMainChain()) {
            list.add(new BlockWrapper(HashingUtils.sha256(block), block.getPrevBlockHash()));
        }
        return list;
    }

    @RequestMapping(value = "/backup", method = RequestMethod.GET)
    public List<List<BlockWrapper>> backupChains() {
        List<List<BlockWrapper>> lists = new ArrayList<List<BlockWrapper>>();
        List<List<Block>> backupChains = miner.getBackupChains();

        for (List<Block> backupChain : backupChains) {
            List<BlockWrapper> list = new ArrayList<BlockWrapper>();
            for (Block block : backupChain) {
                list.add(new BlockWrapper(HashingUtils.sha256(block), block.getPrevBlockHash()));
            }
            lists.add(list);
        }
        return lists;
    }

    @RequestMapping(value = "/valid", method = RequestMethod.GET)
    public Boolean validate() {
        return miner.validateChain();
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    public void shutdown() {
        miner.stopMining();
    }

    static class BlockWrapper {
        private String hash;
        private String prevHash;

        public BlockWrapper(String hash, String prevHash) {
            this.hash = hash;
            this.prevHash = prevHash;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getPrevHash() {
            return prevHash;
        }

        public void setPrevHash(String prevHash) {
            this.prevHash = prevHash;
        }
    }
}
