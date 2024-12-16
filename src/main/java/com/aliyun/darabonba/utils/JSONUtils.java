package com.aliyun.darabonba.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONUtils {
    public static Object parseJSON(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                }.getType(), new MapTypeAdapter()).create();

        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
        return jsonElement.isJsonArray() ? gson.fromJson(json, List.class) :
                gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
    }

    public static String stringify(Object obj) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(obj);
    }

    public static <T> T readPath(Object json, String path) {
        String jsonStr = stringify(json);
        Object result = JsonPath.read(jsonStr, path);
        return (T) convertNumber(result);
    }


    private static Object convertNumber(Object input) {
        if (input instanceof Integer) {
            return Long.valueOf((Integer) input);
        } else if (input instanceof List) {
            List<?> list = (List<?>) input;
            return handleList(list);
        } else if (input instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) input;
            return handleMap(map);
        }
        return input;
    }

    private static List<?> handleList(List<?> list) {
        List<Object> longList = new ArrayList<>();
        for (Object item : list) {
            longList.add(convertNumber(item));
        }
        return longList;
    }

    private static Map<String, ?> handleMap(Map<?, ?> map) {
        Map<String, Object> longMap = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            longMap.put(key, convertNumber(entry.getValue()));
        }
        return longMap;
    }

    static class MapTypeAdapter extends TypeAdapter<Object> {

        private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    /**
                     * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                     */
                    String s1 = in.nextString();
                    if (s1.contains(".") || s1.contains("E")) {
                        return Double.parseDouble(s1);
                    } else {
                        return Long.parseLong(s1);
                    }

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            delegate.write(out, value);
        }
    }
}
