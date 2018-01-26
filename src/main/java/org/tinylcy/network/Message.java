package org.tinylcy.network;

import java.io.Serializable;

/**
 * Created by tinylcy.
 */
public class Message<T> implements Serializable {

    private Peer sender;
    private T data;
    private MessageType type;

    public Message(Peer sender, T data, MessageType type) {
        this.sender = sender;
        this.data = data;
        this.type = type;
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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
