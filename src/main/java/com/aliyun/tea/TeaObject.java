package com.aliyun.tea;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TeaObject<T> {
    
    public Type getParameterizedType()
    {
        Class<?> c = getClass();
        ParameterizedType t = (ParameterizedType)c.getGenericSuperclass();
        Type[] ts = t.getActualTypeArguments();
        return ts[0];
    }

}