package com.aliyun.tea;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TeaException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private Map<String, Object> data;

    public TeaException(Map<String, Object> map) {
        this.setCode((String) map.get("code"));
        this.setMessage((String) map.get("message"));
        Object obj = map.get("data");
        if (obj == null) {
            return;
        }

        Map<String, Object> hashMap = new HashMap<String, Object>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                hashMap.put(field.getName(), field.get(obj));
            } catch (Exception e) {
                continue;
            }
        }
        this.data = hashMap;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}