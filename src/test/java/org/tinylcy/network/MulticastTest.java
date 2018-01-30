package org.tinylcy.network;

import org.tinylcy.common.FastJsonUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by tinylcy.
 */
public class MulticastTest {

    public void testMulticast() throws Exception {
        final Something thing = new Something("tinylcy", 24);
        final String data = FastJsonUtils.getJsonString(thing);

        final Multicast multicast = new Multicast();

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                multicast.send(data);
            }
        });

        Thread receiver1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    multicast.receive();
                }
            }
        });

        Thread receiver2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    multicast.receive();
                }
            }
        });

        receiver1.start();
        receiver2.start();
        TimeUnit.SECONDS.sleep(3);
        sender.start();
    }

    /**
     * Start the JVM with -Djava.net.preferIPv4Stack=true.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new MulticastTest().testMulticast();
    }
}
