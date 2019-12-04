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
    public static <T> Map<String, T> merge(Class<T> t, Map<String, ?>... maps) {
        Map<String, T> out = new HashMap<>();
        for (int i = 0; i < maps.length; i++) {
            Map<String, ?> map = maps[i];
            if (null == map) {
                continue;
            }
            Set<? extends Entry<String, ?>> entries = map.entrySet();
            for (Entry<String, ?> entry : entries) {
                if (null != entry.getValue()) {
                    out.put(entry.getKey(), (T) entry.getValue());
                }
            }
        }
        return out;
    }

}
