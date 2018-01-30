package org.tinylcy.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by tinylcy.
 */
public class InetAddressUtils {

    public static String getIP() {
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
