package com.aliyun.tea;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TeaModel {

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
            if (null != field.get(this) && List.class.isAssignableFrom(field.get(this).getClass())) {
                ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                Type listActualTypeArgument = listActualTypeArguments[0];
                Class<?> itemType = null;
                if (listActualTypeArgument instanceof Class) {
                    itemType = (Class<?>) listActualTypeArgument;
                }
                ArrayList<Object> arrayField = (ArrayList<Object>) field.get(this);
                Map<String, Object> fields;
                ArrayList<Object> fieldList = new ArrayList<>();
                for (int i = 0; i < arrayField.size(); i++) {
                    if (null != itemType && TeaModel.class.isAssignableFrom(itemType)) {
                        fields = ((TeaModel) arrayField.get(i)).toMap();
                        fieldList.add(fields);
                    } else {
                        fieldList.add(arrayField.get(i));
                    }
                }
                map.put(key, fieldList);
            } else if (null != field.get(this) && TeaModel.class.isAssignableFrom(field.get(this).getClass())) {
                TeaModel teaModel = (TeaModel) field.get(this);
                map.put(key, teaModel.toMap());
            } else {
                map.put(key, field.get(this));
            }

        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TeaModel> T toModel(Map<String, ?> map, T model)
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
            Class<?> clazz = field.getType();
            if (List.class.isAssignableFrom(clazz)) {
                ArrayList<?> valueList = (ArrayList<?>) value;
                ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                Type listActualTypeArgument = listActualTypeArguments[0];
                Class<?> itemType = null;
                if (listActualTypeArgument instanceof Class) {
                    itemType = (Class<?>) listActualTypeArgument;
                }
                ArrayList result = new ArrayList();
                if (null != itemType && TeaModel.class.isAssignableFrom(itemType)) {
                    if (valueList.size() > 0 && Map.class.isAssignableFrom(valueList.get(0).getClass())) {
                        for (int i = 0; i < valueList.size(); i++) {
                            Object teaModel = TeaModel.toModel((Map<String, Object>) valueList.get(i),
                                    (TeaModel) itemType.getDeclaredConstructor().newInstance());
                            result.add(teaModel);
                        }
                    } else {
                        for (int i = 0; i < valueList.size(); i++) {
                            result.add(valueList.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < valueList.size(); i++) {
                        Object teaModel = valueList.get(i);
                        result.add(teaModel);
                    }
                }
                field.set(model, result);
            } else {
                if (TeaModel.class.isAssignableFrom(clazz) && Map.class.isAssignableFrom(value.getClass())) {
                    Object data = clazz.getDeclaredConstructor().newInstance();
                    field.set(model, TeaModel.toModel((Map<String, Object>) value, (TeaModel) data));
                } else if (Integer.class.isAssignableFrom(clazz)) {
                    field.set(model, Integer.parseInt(String.valueOf(value)));
                } else if (Double.class.isAssignableFrom(clazz)) {
                    field.set(model, Double.parseDouble(String.valueOf(value)));
                } else if (Long.class.isAssignableFrom(clazz)) {
                    field.set(model, Long.parseLong(String.valueOf(value)));
                } else if (Boolean.class.isAssignableFrom(clazz)) {
                    field.set(model, Boolean.parseBoolean(String.valueOf(value)));
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
        boolean required;
        for (int i = 0; i < fields.length; i++) {
            object = fields[i].get(this);
            validation = fields[i].getAnnotation(Validation.class);
            if (null != validation) {
                required = validation.required();
            } else {
                required = false;
            }
            if (required && null == object) {
                throw new ValidateException("Field " + fields[i].getName() + " is required");
            }
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

    public static Map<String, Object> buildMap(TeaModel teaModel) throws IllegalAccessException {
        if (null == teaModel) {
            return new HashMap<>();
        } else {
            return teaModel.toMap();
        }
    }

    public static void validateParams(TeaModel teaModel, String paramName) throws ValidateException, IllegalAccessException {
        if (null == teaModel) {
            throw new ValidateException("parameter " + paramName + " is not allowed as null");
        }
        teaModel.validate();
    }

    public static Map<String, Object> toMap(Object object) throws IllegalArgumentException, IllegalAccessException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (null != object && object instanceof Map) {
            return (Map<String, Object>) object;
        }
        if (null == object || !TeaModel.class.isAssignableFrom(object.getClass())) {
            return map;
        }
        for (Field field : object.getClass().getFields()) {
            String key = field.getName();
            if (null != field.get(object) && List.class.isAssignableFrom(field.get(object).getClass())) {
                ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                Type listActualTypeArgument = listActualTypeArguments[0];
                Class<?> itemType = null;
                if (listActualTypeArgument instanceof Class) {
                    itemType = (Class<?>) listActualTypeArgument;
                }
                ArrayList<Object> arrayField = (ArrayList<Object>) field.get(object);
                Map<String, Object> fields;
                ArrayList<Object> fieldList = new ArrayList<>();
                for (int i = 0; i < arrayField.size(); i++) {
                    if (null != itemType && TeaModel.class.isAssignableFrom(itemType)) {
                        fields = TeaModel.toMap(arrayField.get(i));
                        fieldList.add(fields);
                    } else {
                        fieldList.add(arrayField.get(i));
                    }
                }
                map.put(key, fieldList);
            } else if (null != field.get(object) && TeaModel.class.isAssignableFrom(field.get(object).getClass())) {
                map.put(key, TeaModel.toMap(field.get(object)));
            } else {
                map.put(key, field.get(object));
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TeaModel> T build(Map<String, ?> map, T model)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        for (Field field : model.getClass().getFields()) {
            String key = field.getName();
            Object value = map.get(key);
            if (value == null) {
                continue;
            }
            Class<?> clazz = field.getType();
            if (List.class.isAssignableFrom(clazz)) {
                ArrayList<?> valueList = (ArrayList<?>) value;
                ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                Type listActualTypeArgument = listActualTypeArguments[0];
                Class<?> itemType = null;
                if (listActualTypeArgument instanceof Class) {
                    itemType = (Class<?>) listActualTypeArgument;
                }
                ArrayList result = new ArrayList();
                if (null != itemType && TeaModel.class.isAssignableFrom(itemType)) {
                    if (valueList.size() > 0 && Map.class.isAssignableFrom(valueList.get(0).getClass())) {
                        for (int i = 0; i < valueList.size(); i++) {
                            Object teaModel = TeaModel.build((Map<String, Object>) valueList.get(i),
                                    (TeaModel) itemType.getDeclaredConstructor().newInstance());
                            result.add(teaModel);
                        }
                    } else {
                        for (int i = 0; i < valueList.size(); i++) {
                            result.add(valueList.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < valueList.size(); i++) {
                        Object teaModel = valueList.get(i);
                        result.add(teaModel);
                    }
                }
                field.set(model, result);
            } else {
                if (TeaModel.class.isAssignableFrom(clazz)) {
                    Object data = clazz.getDeclaredConstructor().newInstance();
                    field.set(model, TeaModel.build(TeaModel.toMap(value), (TeaModel) data));
                } else if (Integer.class.isAssignableFrom(clazz)) {
                    field.set(model, Integer.parseInt(String.valueOf(value)));
                } else if (Double.class.isAssignableFrom(clazz)) {
                    field.set(model, Double.parseDouble(String.valueOf(value)));
                } else if (Long.class.isAssignableFrom(clazz)) {
                    field.set(model, Long.parseLong(String.valueOf(value)));
                } else if (Boolean.class.isAssignableFrom(clazz)) {
                    field.set(model, Boolean.parseBoolean(String.valueOf(value)));
                } else {
                    field.set(model, value);
                }
            }
        }
        return model;
    }
}
