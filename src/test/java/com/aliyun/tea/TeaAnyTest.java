package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

public class TeaAnyTest {
    @Test
    public void newInstance() {
        TeaAny any = new TeaAny(null);
        Assert.assertNull(any.json);
    }
}