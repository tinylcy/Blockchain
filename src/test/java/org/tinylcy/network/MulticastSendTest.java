package org.tinylcy.network;

import org.junit.Test;
import org.tinylcy.common.FastJsonUtils;

/**
 * Created by tinylcy.
 */
public class MulticastSendTest {

    @Test
    public void testSend() {
        Something thing = new Something("tinylcy", 24);
        byte[] bytes = FastJsonUtils.getJsonString(thing).getBytes();
        Multicast multicast = new Multicast();
        multicast.send(bytes);
    }
}
