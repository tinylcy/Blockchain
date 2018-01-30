package org.tinylcy.common;

import org.apache.commons.io.IOUtils;
import org.tinylcy.network.Peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyang li.
 */
public class ConfigurationUtils {

    public static List<Peer> peers() {
        List<Peer> peers = new ArrayList<Peer>();
        BufferedReader reader;
        InputStream input;

        input = ConfigurationUtils.class.getClassLoader().getResourceAsStream("peers.list");
        reader = new BufferedReader(new InputStreamReader(input));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (InetAddressUtils.isValidIP(line)) {
                    peers.add(new Peer(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(reader);
        }

        return peers;

    }
}
