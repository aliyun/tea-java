package com.aliyun.tea;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TeaModel {
    public TeaModel() {

    }

    public Map<String, Object> toMap() throws IllegalArgumentException, IllegalAccessException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Field field : this.getClass().getFields()) {
            NameInMap anno = field.getAnnotation(NameInMap.class);
            String key;
            if (anno == null) {
                key = field.getName();
            } else {
                key = anno.value();
            }

            map.put(key, field.get(this));
        }

        return map;
    }

    public static <T extends TeaModel> T toModel(Map<String, Object> map, T model)
            throws IllegalArgumentException, IllegalAccessException {
        for (Field field : model.getClass().getFields()) {
            NameInMap anno = field.getAnnotation(NameInMap.class);
            String key;
            if (anno == null) {
                key = field.getName();
            } else {
                key = anno.value();
            }
            Object value = map.get(key);
            field.set(model, value);
        }

        return model;
    }
}