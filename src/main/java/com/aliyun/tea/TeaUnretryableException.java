package com.aliyun.tea;

public class TeaUnretryableException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -7006694712718176751L;

    private TeaRequest lastRequest = null;

    public TeaRequest getLastRequest() {
        return lastRequest;
    }

    public TeaUnretryableException(TeaRequest lastRequest, Throwable lastException) {
        super(lastException.getMessage(), lastException);
        this.lastRequest = lastRequest;
    }

    public TeaUnretryableException(TeaRequest lastRequest) {
        this.lastRequest = lastRequest;
    }

    public TeaUnretryableException(Throwable lastException) {
        super(lastException);
    }

    public TeaUnretryableException() {
        super();
    }
}
