package com.aliyun.tea.interceptor;

import com.aliyun.tea.TeaRequest;
import com.aliyun.tea.utils.AttributeMap;

public interface RequestInterceptor {

    TeaRequest modifyRequest(InterceptorContext context, AttributeMap attributes);

}
