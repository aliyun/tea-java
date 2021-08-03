package com.aliyun.tea;

import java.util.Map;

public class TeaRetryableException extends TeaException {

    private static final long serialVersionUID = 3883312421128465122L;

    public TeaRetryableException(Throwable cause) {
        super("", cause);
        message = cause.getMessage();
    }

    public TeaRetryableException(Map<String, ?> map) {
        super(map);
    }

    public TeaRetryableException() {
    }
}