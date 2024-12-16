package com.aliyun.darabonba.interceptor;

import com.aliyun.darabonba.Request;
import com.aliyun.darabonba.utils.AttributeMap;

public interface RequestInterceptor {

    Request modifyRequest(InterceptorContext context, AttributeMap attributes);

}
