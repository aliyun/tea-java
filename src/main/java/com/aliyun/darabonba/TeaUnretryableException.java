package com.aliyun.darabonba;

public class TeaUnretryableException extends RuntimeException {

    private static final long serialVersionUID = -7006694712718176751L;

    private Request lastRequest = null;

    public Request getLastRequest() {
        return lastRequest;
    }

    public TeaUnretryableException(Request lastRequest, Throwable lastException) {
        super(lastException.getMessage(), lastException);
        this.lastRequest = lastRequest;
    }

    public TeaUnretryableException(Request lastRequest) {
        this.lastRequest = lastRequest;
    }

    public TeaUnretryableException(Throwable lastException) {
        super(lastException);
    }

    public TeaUnretryableException() {
        super();
    }
}
