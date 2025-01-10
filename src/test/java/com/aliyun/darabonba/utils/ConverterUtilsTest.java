package com.aliyun.darabonba.utils;

import org.junit.Assert;
import org.junit.Test;

public class ConverterUtilsTest {
    @Test
    public void testConvert() throws Exception {
        String str = "12.01";
        Assert.assertEquals(12, ConverterUtils.parseInt(str));
        Long longVal = 12L;
        Assert.assertEquals(12, ConverterUtils.parseInt(longVal));
        float floatVal = 12.3F;
        Assert.assertEquals(12, ConverterUtils.parseInt(floatVal));
        double doubleVal = 12.3D;
        Assert.assertEquals(12, ConverterUtils.parseInt(doubleVal));

        Integer intVal = 12;
        Assert.assertEquals(12L, ConverterUtils.parseLong(str));
        Assert.assertEquals(12L, ConverterUtils.parseLong(intVal));
        Assert.assertEquals(12L, ConverterUtils.parseLong(longVal));
        Assert.assertEquals(12L, ConverterUtils.parseLong(floatVal));
        Assert.assertEquals(12L, ConverterUtils.parseLong(doubleVal));

        str = "12";
        Assert.assertEquals(12.0f, ConverterUtils.parseFloat(str), 0);
        Assert.assertEquals(12.0f, ConverterUtils.parseFloat(intVal), 0);
        Assert.assertEquals(12.0f, ConverterUtils.parseFloat(longVal), 0);
        Assert.assertEquals(12.3f, ConverterUtils.parseFloat(floatVal), 0);
        Assert.assertEquals(12.3f, ConverterUtils.parseFloat(doubleVal), 0);

        str = "true";
        Assert.assertTrue(ConverterUtils.parseBoolean(str));
        str = "false";
        Assert.assertFalse(ConverterUtils.parseBoolean(str));
        Assert.assertTrue(ConverterUtils.parseBoolean(true));
        Assert.assertFalse(ConverterUtils.parseBoolean(false));

        Integer boolVal = 1;
        Assert.assertTrue(ConverterUtils.parseBoolean(boolVal));
        boolVal = 0;
        Assert.assertFalse(ConverterUtils.parseBoolean(boolVal));


        // test exceptions
        IllegalArgumentException exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            ConverterUtils.parseBoolean("123");
        });

        String expectedMessage = "Cannot convert data to bool.";
        String actualMessage = exception.getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);

        exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            ConverterUtils.parseBoolean(123);
        });

        expectedMessage = "Cannot convert data to bool.";
        actualMessage = exception.getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);

        exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            ConverterUtils.parseInt(null);
        });

        expectedMessage = "Data is null.";
        actualMessage = exception.getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);

        exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            ConverterUtils.parseLong(null);
        });

        expectedMessage = "Data is null.";
        actualMessage = exception.getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);

        exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            ConverterUtils.parseFloat(null);
        });

        expectedMessage = "Data is null.";
        actualMessage = exception.getMessage();
        Assert.assertEquals(expectedMessage, actualMessage);
    }
}
