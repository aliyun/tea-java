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
        Assert.assertFalse(logger1.canLogAtLevel(LogLevel.INFORMATIONAL));
        Assert.assertFalse(logger1.canLogAtLevel(LogLevel.VERBOSE));
        Assert.assertFalse(logger1.canLogAtLevel(LogLevel.NOT_SET));
        Assert.assertFalse(logger1.canLogAtLevel(null));
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
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "5");
        logger = new ClientLogger(LoggerTest.class);
        Assert.assertEquals("test logExceptionAsWarning",
                logger.logExceptionAsError(new RuntimeException("test logExceptionAsWarning")).getMessage());
    }

    @Test
    public void traceTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "verbose");
        DefaultLogger logger = new DefaultLogger(LoggerTest.class);
        logger.trace("");
        logger.trace("test");
        logger.trace("key1\r\t\nkey2");
        logger.trace("test: {}, {}", "key1", "key2");
        logger.trace("test: {}, {}, {}", "key1", "key2", "key3");
    }

    @Test
    public void verboseTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "verbose");
        ClientLogger logger = new ClientLogger(LoggerTest.class);
        logger.verbose("");
        logger.verbose("test");
        logger.verbose("key1\r\t\nkey2");
        logger.verbose("test: {}, {}", "key1", "key2");
        logger.verbose("test: {}, {}, {}", "key1", "key2", "key3");
    }

    @Test
    public void infoTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "debug");
        ClientLogger logger = new ClientLogger(LoggerTest.class);
        logger.info("");
        logger.info("test");
        logger.info("key1\r\t\nkey2");
        logger.info("test: {}, {}", "key1", "key2");
        logger.info("test: {}, {}, {}", "key1", "key2", "key3");
    }

    @Test
    public void warningTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "debug");
        ClientLogger logger = new ClientLogger(LoggerTest.class);
        logger.warning("");
        logger.warning("test");
        logger.warning("key1\r\t\nkey2");
        logger.warning("test: {}, {}", "key1", "key2");
        logger.warning("test: {}, {}, {}", "key1", "key2", "key3");
    }

    @Test
    public void errorTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "debug");
        ClientLogger logger = new ClientLogger(LoggerTest.class);
        logger.error("");
        logger.error("test");
        logger.error("key1\r\t\nkey2");
        logger.error("test: {}, {}", "key1", "key2");
        logger.error("test: {}, {}, {}", "key1", "key2", "key3");
    }
}
