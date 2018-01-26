package org.tinylcy.network;

import java.io.Serializable;

/**
 * Created by tinylcy.
 */
public class Message<T> implements Serializable {

    private Peer sender;
    private T data;

    public Message(Peer sender, T data) {
        this.sender = sender;
        this.data = data;
    }

    public Peer getSender() {
        return sender;
    }

    public void setSender(Peer sender) {
        this.sender = sender;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
