package com.aliyun.tea;

import java.util.Map;

public class TeaRetryableException extends TeaException {

    private static final long serialVersionUID = 3883312421128465122L;

    public TeaRetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeaRetryableException(Map<String, ?> map) {
        super(map);
    }

    public TeaRetryableException(){}
}