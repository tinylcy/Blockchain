package org.tinylcy.chain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Block implements Serializable {

    /* Summary */
    private Long timestamp;
    private List<Transaction> transactions;
    private Long nonce;
    private String merkleRoot;
    private Integer height;
    private Integer size;

    /* Hashes */
    private String hash;
    private String prevBlockHash;
    private String nextBlockHash;

    public Block() {
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public void setPrevBlockHash(String prevBlockHash) {
        this.prevBlockHash = prevBlockHash;
    }

    public String getNextBlockHash() {
        return nextBlockHash;
    }

    public void setNextBlockHash(String nextBlockHash) {
        this.nextBlockHash = nextBlockHash;
    }
}
