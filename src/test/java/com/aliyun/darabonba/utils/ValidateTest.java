package com.aliyun.darabonba.utils;

import org.junit.Assert;
import org.junit.Test;

public class ValidateTest {
    @Test
    public void isTrueTest() {
        try {
            Validate.isTrue(false, "message: %s", "test");
        } catch (Exception e) {
            Assert.assertEquals("message: test", e.getMessage());
        }
    }

    @Test
    public void notNullTest() {
        Object object = Validate.notNull("test", "message: %s", "test");
        Assert.assertNotNull(object);
        Assert.assertEquals("test", object);
        try {
            Validate.notNull(null, "%s is null", "test");
        } catch (Exception e) {
            Assert.assertEquals("test is null", e.getMessage());
        }
    }

    @Test
    public void isNullTest() {
        try {
            Validate.isNull("not null", "message: %s", "test");
        } catch (Exception e) {
            Assert.assertEquals("message: test", e.getMessage());
        }
    }

    @Test
    public void isAssignableFromTest() {
        Class<? extends Object> assignableFrom = Validate.isAssignableFrom(Object.class, String.class, "message: %s", "test");
        Assert.assertNotNull(assignableFrom);

        try {
            Validate.isAssignableFrom(String.class, Object.class, "message: %s", "test");
        } catch (Exception e) {
            Assert.assertEquals("message: test", e.getMessage());
        }
    }
}
