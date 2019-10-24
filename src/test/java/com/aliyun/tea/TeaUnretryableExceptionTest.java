package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

public class TeaUnretryableExceptionTest {
    @Test
    public void TestUnretryableException() {
        TeaUnretryableException exception = new TeaUnretryableException(TeaRequest.create());
        Assert.assertEquals(null, exception.getMessage());
    }
}
