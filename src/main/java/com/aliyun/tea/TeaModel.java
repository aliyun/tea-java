package com.aliyun.tea;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

    @SuppressWarnings("unchecked")
    public static <T extends TeaModel> T toModel(Map<String, Object> map, T model)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        for (Field field : model.getClass().getFields()) {
            NameInMap anno = field.getAnnotation(NameInMap.class);
            String key;
            if (anno == null) {
                key = field.getName();
            } else {
                key = anno.value();
            }
            Object value = map.get(key);
            if (value == null) {
                continue;
            }

            if (field.getType().isArray() && value instanceof ArrayList) {
                Class<?> itemType = field.getType().getComponentType();
                ArrayList<?> valueList = (ArrayList<?>) value;
                Object[] target = (Object[]) Array.newInstance(itemType, valueList.size());
                for (int i = 0; i < valueList.size(); i++) {
                    Array.set(target, i, valueList.get(i));
                }
                field.set(model, target);
            } else {
                Class<?> clazz = field.getType();
                if (TeaModel.class.isAssignableFrom(clazz)) {
                    Object data = clazz.getDeclaredConstructor().newInstance();
                    field.set(model, TeaModel.toModel((Map<String, Object>)value, (TeaModel)data));
                } else {
                    field.set(model, value);
                }
            }
        }

        return model;
    }
}