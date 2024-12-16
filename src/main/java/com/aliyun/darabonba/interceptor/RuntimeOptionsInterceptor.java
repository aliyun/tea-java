package com.aliyun.darabonba.interceptor;

import com.aliyun.darabonba.utils.AttributeMap;

import java.util.Map;

public interface RuntimeOptionsInterceptor {

    Map<String, Object> modifyRuntimeOptions(InterceptorContext context, AttributeMap attributes);

}
