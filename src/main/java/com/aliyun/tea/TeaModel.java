package com.aliyun.tea;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TeaModel {

    public Map<String, Object> toMap() {
        return changeToMap(this);
    }

    public static Map<String, Object> toMap(Object object) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != object && object instanceof Map) {
            return (Map<String, Object>) object;
        }
        if (null == object || !TeaModel.class.isAssignableFrom(object.getClass())) {
            return map;
        }
        map = changeToMap(object);
        return map;
    }

    private static Map<String, Object> changeToMap(Object object) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            for (Field field : object.getClass().getFields()) {
                NameInMap anno = field.getAnnotation(NameInMap.class);
                String key;
                if (anno == null) {
                    key = field.getName();
                } else {
                    key = anno.value();
                }
                if (null != field.get(object) && List.class.isAssignableFrom(field.get(object).getClass())) {
                    List<Object> arrayField = (List<Object>) field.get(object);
                    List<Object> fieldList = new ArrayList<Object>();
                    for (int i = 0; i < arrayField.size(); i++) {
                        fieldList.add(parseObject(arrayField.get(i)));
                    }
                    map.put(key, fieldList);
                } else if (null != field.get(object) && TeaModel.class.isAssignableFrom(field.get(object).getClass())) {
                    map.put(key, TeaModel.toMap(field.get(object)));
                } else if (null != field.get(object) && Map.class.isAssignableFrom(field.get(object).getClass())) {
                    Map<String, Object> valueMap = (Map<String, Object>) field.get(object);
                    Map<String, Object> result = new HashMap<String, Object>();
                    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                        result.put(entry.getKey(), parseObject(entry.getValue()));
                    }
                    map.put(key, result);
                } else {
                    map.put(key, field.get(object));
                }
            }
        } catch (Exception e) {
            throw new TeaException(e.getMessage(), e);
        }
        return map;
    }


    private static Object parseObject(Object o) {
        Class clazz = o.getClass();
        if (List.class.isAssignableFrom(clazz)) {
            List<Object> list = (List<Object>) o;
            List<Object> result = new ArrayList<Object>();
            for (Object object : list) {
                result.add(parseObject(object));
            }
            return result;
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<String, Object> map = (Map<String, Object>) o;
            Map<String, Object> result = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                result.put(entry.getKey(), parseObject(entry.getValue()));
            }
            return result;
        } else if (TeaModel.class.isAssignableFrom(clazz)) {
            return ((TeaModel) o).toMap();
        } else {
            return o;
        }
    }

    private static Object buildObject(Object o, Class self, Type subType) {
        Class valueClass = o.getClass();
        if (Map.class.isAssignableFrom(self) && Map.class.isAssignableFrom(valueClass)) {
            Map<String, Object> valueMap = (Map<String, Object>) o;
            Map<String, Object> result = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                if (null == subType || subType instanceof WildcardTypeImpl) {
                    result.put(entry.getKey(), entry.getValue());
                } else if (subType instanceof Class) {
                    result.put(entry.getKey(), buildObject(entry.getValue(), (Class) subType, null));
                } else {
                    ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) subType;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    result.put(entry.getKey(), buildObject(entry.getValue(), parameterizedType.getRawType(), types[types.length - 1]));
                }
            }
            return result;
        } else if (List.class.isAssignableFrom(self) && List.class.isAssignableFrom(valueClass)) {
            List<Object> valueList = (List<Object>) o;
            List<Object> result = new ArrayList<Object>();
            for (Object object : valueList) {
                if (null == subType || subType instanceof WildcardTypeImpl) {
                    result.add(object);
                } else if (subType instanceof Class) {
                    result.add(buildObject(object, (Class) subType, null));
                } else {
                    ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) subType;
                    Type[] types = parameterizedType.getActualTypeArguments();
                    result.add(buildObject(object, parameterizedType.getRawType(), types[types.length - 1]));
                }
            }
            return result;
        } else if (TeaModel.class.isAssignableFrom(self) && Map.class.isAssignableFrom(valueClass)) {
            try {
                return TeaModel.toModel((Map<String, Object>) o, (TeaModel) self.newInstance());
            } catch (Exception e) {
                throw new TeaException(e.getMessage(), e);
            }
        } else {
            return o;
        }
    }

    private static Type getType(Field field, int index) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[index];
        return actualTypeArgument;
    }


    @SuppressWarnings("unchecked")
    public static <T extends TeaModel> T toModel(Map<String, ?> map, T model) {
        T result = model;
        for (Field field : result.getClass().getFields()) {
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
            result = setTeaModelField(result, field, value, false);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T extends TeaModel> T setTeaModelField(T model, Field field, Object value, boolean userBuild) {
        try {
            Class<?> clazz = field.getType();
            Object resultValue = parseNumber(value, clazz);
            T result = model;
            if (TeaModel.class.isAssignableFrom(clazz)) {
                Object data = clazz.getDeclaredConstructor().newInstance();
                if (userBuild) {
                    field.set(result, TeaModel.build(TeaModel.toMap(resultValue), (TeaModel) data));
                } else if (!userBuild && Map.class.isAssignableFrom(resultValue.getClass())) {
                    field.set(result, TeaModel.toModel((Map<String, Object>) resultValue, (TeaModel) data));
                } else {
                    field.set(result, resultValue);
                }
            } else if (Map.class.isAssignableFrom(clazz)) {
                field.set(result, buildObject(resultValue, Map.class, getType(field, 1)));
            } else if (List.class.isAssignableFrom(clazz)) {
                field.set(result, buildObject(resultValue, List.class, getType(field, 0)));
            } else if (Integer.class.isAssignableFrom(clazz)) {
                field.set(result, Integer.parseInt(String.valueOf(resultValue)));
            } else if (Double.class.isAssignableFrom(clazz)) {
                field.set(result, Double.parseDouble(String.valueOf(resultValue)));
            } else if (Float.class.isAssignableFrom(clazz)) {
                field.set(result, Float.parseFloat(String.valueOf(resultValue)));
            } else if (Long.class.isAssignableFrom(clazz)) {
                field.set(result, Long.parseLong(String.valueOf(resultValue)));
            } else if (Boolean.class.isAssignableFrom(clazz)) {
                field.set(result, Boolean.parseBoolean(String.valueOf(resultValue)));
            } else {
                field.set(result, resultValue);
            }
            return result;
        } catch (Exception e) {
            throw new TeaException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends TeaModel> T build(Map<String, ?> map, T model) {
        T result = model;
        for (Field field : model.getClass().getFields()) {
            String key = field.getName();
            Object value = map.get(key);
            if (value == null) {
                NameInMap anno = field.getAnnotation(NameInMap.class);
                if (null == anno) {
                    continue;
                }
                key = anno.value();
                value = map.get(key);
                if (null == value) {
                    continue;
                }
            }
            result = setTeaModelField(result, field, value, true);
        }
        return result;
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
        if (value instanceof Double && (clazz == Float.class || clazz == float.class)) {
            bigDecimal = new BigDecimal(value.toString());
            return bigDecimal.floatValue();
        }
        return value;
    }

    public void validate() {
        Field[] fields = this.getClass().getFields();
        Object object;
        Validation validation;
        String pattern;
        int maxLength;
        int minLength;
        boolean required;
        try {
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
                    minLength = validation.minLength();
                    if (!"".equals(pattern)) {
                        determineType(fields[i].getType(), object, pattern, maxLength, minLength);
                    }
                }
            }
        } catch (Exception e) {
            throw new ValidateException(e.getMessage(), e);
        }
    }

    private void determineType(Class clazz, Object object, String pattern, int maxLength, int minLength) {
        boolean notException = true;
        if (Map.class.isAssignableFrom(clazz)) {
            validateMap(pattern, maxLength, minLength, (Map<String, Object>) object);
        } else if (TeaModel.class.isAssignableFrom(clazz)) {
            ((TeaModel) object).validate();
        } else if (List.class.isAssignableFrom(clazz)) {
            List<?> list = (List<?>) object;
            for (int j = 0; j < list.size(); j++) {
                determineType(list.get(j).getClass(), list.get(j), pattern, maxLength, minLength);
            }
        } else if (clazz.isArray()) {
            Object[] objects = (Object[]) object;
            for (int j = 0; j < objects.length; j++) {
                determineType(clazz.getComponentType(), objects[j], pattern, maxLength, minLength);
            }
        } else {
            String value = String.valueOf(object);
            if (maxLength > 0) {
                notException = value.length() <= maxLength;
            }
            if (notException && minLength > 0) {
                notException = value.length() >= minLength;
            }
            if (notException) {
                notException = Pattern.matches(pattern, value);
            }
            if (!notException) {
                throw new ValidateException("param don't matched");
            }
        }
    }

    private void validateMap(String pattern, int maxLength, int minLength, Map<String, Object> map) {
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() != null) {
                determineType(entry.getValue().getClass(), entry.getValue(), pattern, maxLength, minLength);
            }
        }
    }

    public static Map<String, Object> buildMap(TeaModel teaModel) {
        if (null == teaModel) {
            return null;
        } else {
            return teaModel.toMap();
        }
    }

    public static void validateParams(TeaModel teaModel, String paramName) {
        if (null == teaModel) {
            throw new ValidateException("parameter " + paramName + " is not allowed as null");
        }
        teaModel.validate();
    }


}
