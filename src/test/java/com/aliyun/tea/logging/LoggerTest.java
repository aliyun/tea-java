package com.aliyun.tea.logging;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

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

    @Test
    public void defaultLoggerOverloadsTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "verbose");
        DefaultLogger logger = new DefaultLogger("com.aliyun.tea.logging.NotExistClass");
        Assert.assertEquals("com.aliyun.tea.logging.NotExistClass", logger.getName());

        logger.trace("trace {}", "a");
        logger.trace("trace", new RuntimeException("t"));
        logger.debug("debug {}", "a");
        logger.debug("debug {} {}", "a", "b");
        logger.debug("debug", new RuntimeException("d"));
        logger.info("info {}", "a");
        logger.info("info {} {}", "a", "b");
        logger.info("info", new RuntimeException("i"));
        logger.warn("warn {}", "a");
        logger.warn("warn {} {}", "a", "b");
        logger.warn("warn", new RuntimeException("w"));
        logger.error("error {}", "a");
        logger.error("error {} {}", "a", "b");
        logger.error("error", new RuntimeException("e"));
    }

    @Test
    public void logLevelFromStringNullTest() {
        Assert.assertEquals(LogLevel.NOT_SET, LogLevel.fromString(null));
    }

    @Test
    public void clientLoggerThrowableInArgsTest() {
        System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "warn");
        ClientLogger logger = new ClientLogger(LoggerTest.class);
        logger.warning("warn with throwable: {}", "x", new RuntimeException("warn-ex"));
        logger.error("error with throwable: {}", "y", new RuntimeException("error-ex"));
        logger.verbose("empty-args");
        logger.verbose("format-only", new Object[0]);
    }

    @Test
    public void clientLoggerDisabledBranchesTest() throws Exception {
        String previous = System.getProperty(DefaultLogger.SDK_LOG_LEVEL);
        try {
            System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "5");
            ClientLogger disabled = new ClientLogger(LoggerTest.class);
            disabled.info("info");
            disabled.info("info {}", "a");
            disabled.warning("warn");
            disabled.warning("warn {}", "a");
            disabled.error("error");
            disabled.error("error {}", "a");

            System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "verbose");
            ClientLogger verbose = new ClientLogger(LoggerTest.class);
            verbose.verbose("no-args", new Object[0]);

            Method performLogging = ClientLogger.class.getDeclaredMethod(
                    "performLogging", LogLevel.class, boolean.class, String.class, Object[].class);
            performLogging.setAccessible(true);
            performLogging.invoke(verbose, LogLevel.NOT_SET, false, "unused", new Object[]{"a"});
        } finally {
            restoreLogProperty(previous);
        }
    }

    @Test
    public void traceLevelBranchTest() throws Exception {
        String previous = System.getProperty(DefaultLogger.SDK_LOG_LEVEL);
        Field numeric = LogLevel.class.getDeclaredField("numericValue");
        int original = LogLevel.VERBOSE.getLogLevel();
        boolean mutated = setFinalInt(numeric, LogLevel.VERBOSE, 10);
        try {
            System.setProperty(DefaultLogger.SDK_LOG_LEVEL, "warn");
            DefaultLogger logger = new DefaultLogger(LoggerTest.class);
            if (!mutated && !setFinalBoolean(logger, "isTraceEnabled", true)) {
                return;
            }
            Assert.assertTrue(logger.isTraceEnabled());
            logger.trace("covered");
            logger.trace("covered {}", "arg");
        } finally {
            if (mutated) {
                setFinalInt(numeric, LogLevel.VERBOSE, original);
            }
            restoreLogProperty(previous);
        }
    }

    @Test
    public void logLevelFromEnvironmentTest() throws Exception {
        String key = DefaultLogger.SDK_LOG_LEVEL;
        String previous = System.getProperty(key);
        boolean changed = setEnv(key, "error");
        try {
            if (!changed || !"error".equals(System.getenv(key))) {
                return;
            }
            DefaultLogger logger = new DefaultLogger(LoggerTest.class);
            Assert.assertTrue(logger.isErrorEnabled());
            Assert.assertFalse(logger.isWarnEnabled());
            Assert.assertFalse(logger.isInfoEnabled());
        } finally {
            if (changed) {
                setEnv(key, null);
            }
            restoreLogProperty(previous);
        }
    }

    private static void restoreLogProperty(String previous) {
        if (previous == null) {
            System.clearProperty(DefaultLogger.SDK_LOG_LEVEL);
        } else {
            System.setProperty(DefaultLogger.SDK_LOG_LEVEL, previous);
        }
    }

    private static boolean setFinalInt(Field field, Object target, int value) {
        try {
            field.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.setInt(target, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean setFinalBoolean(Object target, String name, boolean value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.setBoolean(target, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean setEnv(String key, String value) {
        try {
            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironment = processEnvironment.getDeclaredField("theEnvironment");
            theEnvironment.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironment.get(null);
            if (value == null) {
                env.remove(key);
            } else {
                env.put(key, value);
            }
            try {
                Field caseInsensitive = processEnvironment.getDeclaredField("theCaseInsensitiveEnvironment");
                caseInsensitive.setAccessible(true);
                Map<String, String> ci = (Map<String, String>) caseInsensitive.get(null);
                if (value == null) {
                    ci.remove(key);
                } else {
                    ci.put(key, value);
                }
            } catch (NoSuchFieldException ignored) {
                // non-Windows JDKs do not expose the case-insensitive view
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
