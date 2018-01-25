package org.tinylcy.consensus.pow;

import com.google.common.hash.Hashing;
import org.tinylcy.chain.Block;
import org.tinylcy.common.FastJsonUtils;

import java.nio.charset.StandardCharsets;

/**
 * Created by tinylcy.
 */
public class Pow {

    public Long mine(Block block) {
        Long nonce;
        String sha256;

        sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block), StandardCharsets.UTF_8).toString();
        for (nonce = 0L; !isValid(sha256); nonce++) {
            sha256 = Hashing.sha256().hashString(FastJsonUtils.getJsonString(block) + nonce, StandardCharsets.UTF_8).toString();
            // System.out.println("sha256: " + sha256 + ", nonce: " + nonce);
        }
        return nonce;
    }


    private Boolean isValid(String proof) {
        return proof.startsWith("00000");
    }
}
