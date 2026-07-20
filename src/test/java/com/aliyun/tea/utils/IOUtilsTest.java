package com.aliyun.tea.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class IOUtilsTest {

    @Test
    public void closeQuietlyTest() {
        byte[] source = {66, 99};
        InputStream inputStream = new ByteArrayInputStream(source);
        try {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(null);
            IOUtils.closeQuietly(new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    throw new IOException("close failed");
                }
            });
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void closeIfCloseableTest() {
        try {
            IOUtils.closeIfCloseable("test");
            IOUtils.closeIfCloseable(new ByteArrayInputStream(new byte[]{1}));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
