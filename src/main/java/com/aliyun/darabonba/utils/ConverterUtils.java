package com.aliyun.darabonba.utils;

public class ConverterUtils {
    public static <T> int parseInt(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null.");
        }
        return (int) Double.parseDouble(data.toString());
    }

    public static <T> long parseLong(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null.");
        }
        return (long) Double.parseDouble(data.toString());
    }

    public static <T> float parseFloat(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null.");
        }
        return (float) Double.parseDouble(data.toString());
    }
    
    public static <T> boolean parseBoolean(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null.");
        }
        String stringValue = data.toString();
        if ("true".equals(stringValue) || "1".equals(stringValue)) {
            return true;
        }
        if ("false".equals(stringValue) || "0".equals(stringValue)) {
            return false;
        }
        throw new IllegalArgumentException("Cannot convert data to bool.");
    }
}
