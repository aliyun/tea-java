package com.aliyun.tea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TeaExceptionTest {
    private class Testclass{
        private int test = 1;
    }
    @Test
    public void toTeaExceptionTest() {
            TeaException exception = new TeaException(new HashMap<String, Object>());
            exception.getMessage();
            exception.getData();
            exception.getCode();
            Map<String, Object> map = new HashMap<>(1 << 4);
            map.put("data", new Testclass());
            exception = new TeaException(map);
            Assert.assertEquals(1, exception.getData().get("test"));
        }
}
