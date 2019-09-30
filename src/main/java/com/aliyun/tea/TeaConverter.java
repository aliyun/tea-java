package com.aliyun.tea;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TeaConverter {

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> buildMap(TeaPair... pairs) {
        Map<String, T> map = new HashMap<String, T>();
        for (int i = 0; i < pairs.length; i++) {
            TeaPair pair = pairs[i];
            map.put(pair.key, (T) pair.value);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> merge(Map<String, Object>... maps) {
        Map<String, Object> out = new HashMap<String, Object>();
        for (int i = 0; i < maps.length; i++) {
            Map<String, Object> map = maps[i];
            Set<Entry<String, Object>> entries = map.entrySet();
            for (Entry<String, Object> entry : entries) {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }

}
