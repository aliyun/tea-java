package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

public class TeaRetryableExceptionTest {
    @Test
    public void teaRetryableException() {
        TeaRetryableException exception = new TeaRetryableException();
        Assert.assertEquals(null, exception.getMessage());
    }
}
