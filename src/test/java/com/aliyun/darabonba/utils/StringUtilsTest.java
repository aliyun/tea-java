package com.aliyun.darabonba.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class StringUtilsTest {
    @Test
    public void init() {
        Assert.assertNotNull(new StringUtils());
    }

    @Test
    public void isEmpty() {
        Object object = null;
        Assert.assertTrue("null should be true", StringUtils.isEmpty(object));
        Assert.assertTrue("null should be true", StringUtils.isEmpty(null));
        Assert.assertTrue("empty string should be true", StringUtils.isEmpty(""));
        Assert.assertFalse("other string should be false", StringUtils.isEmpty("ok"));
        Assert.assertFalse("other should be false",StringUtils.isEmpty(21321));
    }

    @Test
    public void joinTest() {
        ArrayList<String> list = new ArrayList<String>();
        String str = StringUtils.join(";", list);
        Assert.assertEquals("", str);

        list.add("test");
        list.add("test");
        str = StringUtils.join(";", list);
        Assert.assertEquals(str, "test;test");
    }
}


