package com.aliyun.tea;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TeaConverter {

    public static Map<String, Object> toMap(Object obj) {
        if (null == obj) {
            return new HashMap<>(16);
        }
        String map0 = new Gson().toJson(obj);
        Map<?, ?> map = new Gson().fromJson(map0, Map.class);
        return (Map<String, Object>) map;
    }

    public static <T> T toObject(Map<?, ?> map, TeaObject<T> teaObject) {
        final Type t = teaObject.getParameterizedType();
        String jsonMap = new Gson().toJson(map, Map.class);
        T object = new Gson().fromJson(jsonMap, t);
        return object;
    }

    public static <T> Map<String, T> buildMap(TeaPair... pairs) {
        Map<String, T> map = new HashMap<String, T>();
        for (int i = 0; i < pairs.length; i++) {
            TeaPair pair = pairs[i];
            map.put(pair.key, (T) pair.value);
        }
        return map;
    }

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
