package org.tinylcy.network;

import org.junit.Test;
import org.tinylcy.chain.Block;
import org.tinylcy.common.FastJsonUtils;
import org.tinylcy.common.InetAddressUtils;

/**
 * Created by tinylcy.
 */
public class MulticastSendTest {

    @Test
    public void testSendSomething() {
        Something thing = new Something("tinylcy", 24);
        byte[] bytes = FastJsonUtils.getJsonString(thing).getBytes();
        Multicast multicast = new Multicast();
        multicast.send(bytes);
    }

    @Test
    public void testSendMessage() {
        Message msg = new Message(new Peer(InetAddressUtils.getIP()), new Block(), MessageType.BLOCK);
    }
}
