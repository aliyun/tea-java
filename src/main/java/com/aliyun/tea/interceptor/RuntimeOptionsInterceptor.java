package com.aliyun.tea.interceptor;

import com.aliyun.tea.utils.AttributeMap;

import java.util.Map;

public interface RuntimeOptionsInterceptor {

    Map<String, Object> modifyRuntimeOptions(InterceptorContext context, AttributeMap attributes);

}
