package org.tinylcy.network;

import java.io.Serializable;

/**
 * Created by tinylcy.
 */
public class Message implements Serializable {

    private Peer sender;
    private Object data;
    private MessageType type;

    public Message() {
    }

    public Message(Peer sender, Object data, MessageType type) {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", data=" + data +
                ", type=" + type +
                '}';
    }
}
