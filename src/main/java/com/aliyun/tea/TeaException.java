package com.aliyun.tea;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;

public class TeaException extends Exception {

    private Integer errCode;
    private String errMsg;
    private Map<String, Object> data;

    public TeaException(Map<String, Object> map) {
        this.errCode = (Integer)map.get("code");
        this.errMsg = (String)map.get("message");
        Object obj = map.get("data");
        if(obj == null){    
            return;    
        }   
  
        Map<String, Object> hashMap = new HashMap<String, Object>();    
  
        Field[] declaredFields = obj.getClass().getDeclaredFields();    
        for (Field field : declaredFields) {    
            field.setAccessible(true);  
            try {
                hashMap.put(field.getName(), field.get(obj));  
            } catch(Exception e) {
                continue;
            }
        }
        this.data = hashMap;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}