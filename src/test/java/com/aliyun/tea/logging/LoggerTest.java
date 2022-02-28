package com.aliyun.tea.logging;

import org.junit.Assert;
import org.junit.Test;

public class LoggerTest {

    @Test
    public void baseTest() {
        DefaultLogger logger = new DefaultLogger(LoggerTest.class);
        Assert.assertEquals("com.aliyun.tea.logging.LoggerTest", logger.getName());
        logger.debug("test debug with exception", new Exception("exception"));
    }

    @Test
    public void logLevelTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "warn");
        DefaultLogger logger = new DefaultLogger(LoggerTest.class);
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertFalse(logger.isDebugEnabled());
        Assert.assertFalse(logger.isInfoEnabled());
        Assert.assertFalse(logger.isTraceEnabled());
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "debug");
        logger = new DefaultLogger(LoggerTest.class);
        Assert.assertTrue(logger.isWarnEnabled());
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertTrue(logger.isDebugEnabled());
        Assert.assertTrue(logger.isInfoEnabled());
        Assert.assertFalse(logger.isTraceEnabled());
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "err");
        logger = new DefaultLogger(LoggerTest.class);
        Assert.assertFalse(logger.isWarnEnabled());
        Assert.assertTrue(logger.isErrorEnabled());
        Assert.assertFalse(logger.isDebugEnabled());
        Assert.assertFalse(logger.isInfoEnabled());
        Assert.assertFalse(logger.isTraceEnabled());
        try {
            System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "test");
            logger = new DefaultLogger(LoggerTest.class);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
            Assert.assertEquals("We currently do not support the log level you set. LogLevel: test",
                    e.getMessage());
        }
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "warn");
        ClientLogger logger1 = new ClientLogger(LoggerTest.class);
        Assert.assertTrue(logger1.canLogAtLevel(LogLevel.WARNING));
        Assert.assertTrue(logger1.canLogAtLevel(LogLevel.ERROR));
        Assert.assertFalse(logger1.canLogAtLevel(LogLevel.VERBOSE));
    }

    @Test
    public void doesArgsHaveThrowableTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "debug");
        ClientLogger logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsWarning(new RuntimeException("test logExceptionAsWarning")).getMessage());
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "warn");
        logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsWarning(new RuntimeException("test logExceptionAsWarning")).getMessage());
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "error");
        logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsWarning(new RuntimeException("test logExceptionAsWarning")).getMessage());

        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "debug");
        logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsError(new RuntimeException("test logExceptionAsWarning")).getMessage());
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "warn");
        logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsError(new RuntimeException("test logExceptionAsWarning")).getMessage());
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "error");
        logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsError(new RuntimeException("test logExceptionAsWarning")).getMessage());

    }
}
