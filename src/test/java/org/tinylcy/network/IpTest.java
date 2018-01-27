package org.tinylcy.network;

import org.junit.Test;

import java.net.InetAddress;

/**
 * Created by tinylcy.
 */
public class IpTest {

    @Test
    public void testGetIp() throws Exception {
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Current IP address: " + ip.getHostAddress());
    }
}
