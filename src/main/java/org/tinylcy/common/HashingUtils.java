package org.tinylcy.common;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * Created by tinylcy.
 */
public class HashingUtils {

    public static String sha256(Object obj) {
        return Hashing.sha256().hashString(obj.toString(), StandardCharsets.UTF_8).toString();
    }

    public static String sha256(String str) {
        return Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
    }
}
