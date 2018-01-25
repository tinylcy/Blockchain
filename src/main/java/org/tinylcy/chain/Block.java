package org.tinylcy.chain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tinylcy.
 */
public class Block implements Serializable {

    private Long index;
    private Long timestamp;
    private List<Transaction> transactions;
    private Long nonce;
    private String prevHash;

    public Block() {
    }

    public Block(Long index, Long timestamp, List<Transaction> transactions, Long nonce, String prevHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.nonce = nonce;
        this.prevHash = prevHash;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }
}
