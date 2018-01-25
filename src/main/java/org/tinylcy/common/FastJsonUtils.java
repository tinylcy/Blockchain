package org.tinylcy.common;

import com.alibaba.fastjson.JSON;

/**
 * Created by tinylcy.
 */
public class FastJsonUtils {

    public static String getJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }
}
