package com.aliyun.tea;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
            if (field.getType().isArray() && null != field.get(this)) {
                Object[] arrayField = (Object[]) field.get(this);
                Map<String, Object> fields;
                ArrayList<Object> fieldList = new ArrayList<>();
                for (int i = 0; i < arrayField.length; i++) {
                    if (TeaModel.class.isAssignableFrom(field.getType().getComponentType())) {
                        fields = ((TeaModel) Array.get(arrayField, i)).toMap();
                        fieldList.add(fields);
                    } else {
                        fieldList.add(Array.get(arrayField, i));
                    }
                }
                map.put(key, fieldList);
            } else {
                map.put(key, field.get(this));
            }

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
            value = parseNumber(value, field.getType());
            if (field.getType().isArray() && value instanceof ArrayList) {
                Class<?> itemType = field.getType().getComponentType();
                ArrayList<?> valueList = (ArrayList<?>) value;
                Object[] target = (Object[]) Array.newInstance(itemType, valueList.size());
                if (Map.class.isAssignableFrom(itemType) || TeaModel.class.isAssignableFrom(itemType)) {
                    for (int i = 0; i < valueList.size(); i++) {
                        Array.set(target, i, TeaModel.toModel((Map<String, Object>) valueList.get(i),
                                (TeaModel) itemType.getDeclaredConstructor().newInstance()));
                    }
                } else {
                    for (int i = 0; i < valueList.size(); i++) {
                        Array.set(target, i, valueList.get(i));
                    }
                }
                field.set(model, target);
            } else {
                Class<?> clazz = field.getType();
                if (TeaModel.class.isAssignableFrom(clazz)) {
                    Object data = clazz.getDeclaredConstructor().newInstance();
                    field.set(model, TeaModel.toModel((Map<String, Object>) value, (TeaModel) data));
                } else {
                    field.set(model, value);
                }
            }
        }

        return model;
    }

    private static Object parseNumber(Object value, Class clazz) {
        BigDecimal bigDecimal;
        if (value instanceof Double && (clazz == Long.class || clazz == long.class)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.longValue();
        }
        if (value instanceof Double && (clazz == Integer.class || clazz == int.class)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.intValue();
        }
        return value;
    }

    public void validate() throws IllegalAccessException, ValidateException {
        Field[] fields = this.getClass().getFields();
        Object object;
        Validation validation;
        String pattern;
        int maxLength;
        for (int i = 0; i < fields.length; i++) {
            object = fields[i].get(this);
            validation = fields[i].getAnnotation(Validation.class);
            if (null != validation && null != object) {
                pattern = validation.pattern();
                maxLength = validation.maxLength();
                if (!"".equals(pattern)) {
                    determineType(fields[i].getType(), object, pattern, maxLength);
                }
            }
        }
    }

    private void determineType(Class clazz, Object object, String pattern, int maxLength) throws IllegalAccessException, ValidateException {
        boolean notException = true;
        if (Map.class.isAssignableFrom(clazz)) {
            validateMap(pattern, maxLength, (Map<String, Object>) object);
        } else if (TeaModel.class.isAssignableFrom(clazz)) {
            ((TeaModel) object).validate();
        } else if (List.class.isAssignableFrom(clazz)) {
            List<?> list = (List<?>) object;
            for (int j = 0; j < list.size(); j++) {
                determineType(list.get(j).getClass(), list.get(j), pattern, maxLength);
            }
        } else if (clazz.isArray()) {
            Object[] objects = (Object[]) object;
            for (int j = 0; j < objects.length; j++) {
                determineType(clazz.getComponentType(), objects[j], pattern, maxLength);
            }
        } else {
            String value = String.valueOf(object);
            if (maxLength > 0) {
                notException = value.length() == maxLength;
            }
            if (notException) {
                notException = Pattern.matches(pattern, value);
            }
            if (!notException) {
                throw new ValidateException("param don't matched");
            }
        }
    }

    private void validateMap(String pattern, int maxLength, Map<String, Object> map) throws IllegalAccessException, ValidateException {
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() != null) {
                determineType(entry.getValue().getClass(), entry.getValue(), pattern, maxLength);
            }
        }
    }
}