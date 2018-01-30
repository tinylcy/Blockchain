package org.tinylcy.common;

import com.alibaba.fastjson.JSON;
import org.tinylcy.network.Message;

/**
 * Created by tinylcy.
 */
public class FastJsonUtils {

    public static String getJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static Message parseMessage(byte[] bytes) {
        return JSON.parseObject(bytes, Message.class);
    }

    public static <T> T parseObject(String string, Class<T> clazz) {
        return JSON.parseObject(string, clazz);
    }
}
