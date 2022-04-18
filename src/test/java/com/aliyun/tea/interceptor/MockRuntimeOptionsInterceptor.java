package com.aliyun.tea.interceptor;

import com.aliyun.tea.utils.AttributeMap;

import java.util.Map;

public class MockRuntimeOptionsInterceptor implements RuntimeOptionsInterceptor {

    @Override
    public Map<String, Object> modifyRuntimeOptions(InterceptorContext context, AttributeMap attributes) {
        Map<String, Object> runtimeOptions = context.runtimeOptions();
        runtimeOptions.put("test", "test");
        runtimeOptions.put("key", "test");
        return runtimeOptions;
    }

}
