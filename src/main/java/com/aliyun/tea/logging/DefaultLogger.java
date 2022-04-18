package com.aliyun.tea.logging;

import com.aliyun.tea.utils.StringUtils;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DefaultLogger extends MarkerIgnoringBase {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String WHITESPACE = " ";
    private static final String HYPHEN = " - ";
    private static final String OPEN_BRACKET = " [";
    private static final String CLOSE_BRACKET = "]";
    public static final String WARN = "WARN";
    public static final String DEBUG = "DEBUG";
    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";
    public static final String TRACE = "TRACE";
    public static final String SDK_LOG_LEVEL = "ALIBABA_CLOUD_SDK_LOG_LEVEL";

    private final String classPath;
    private final boolean isTraceEnabled;
    private final boolean isDebugEnabled;
    private final boolean isInfoEnabled;
    private final boolean isWarnEnabled;
    private final boolean isErrorEnabled;

    public DefaultLogger(Class<?> clazz) {
        this(clazz.getName());
    }

    public DefaultLogger(String className) {
        String classPath;
        try {
            classPath = Class.forName(className).getCanonicalName();
        } catch (ClassNotFoundException e) {
            classPath = className;
        }
        this.classPath = classPath;
        int configuredLogLevel = LogLevel.fromString(!StringUtils.isEmpty(loadFromEnvironment(SDK_LOG_LEVEL)) ?
                loadFromEnvironment(SDK_LOG_LEVEL) : WARN)
                .getLogLevel();

        isTraceEnabled = LogLevel.VERBOSE.getLogLevel() > configuredLogLevel;
        isDebugEnabled = LogLevel.VERBOSE.getLogLevel() >= configuredLogLevel;
        isInfoEnabled = LogLevel.INFORMATIONAL.getLogLevel() >= configuredLogLevel;
        isWarnEnabled = LogLevel.WARNING.getLogLevel() >= configuredLogLevel;
        isErrorEnabled = LogLevel.ERROR.getLogLevel() >= configuredLogLevel;
    }

    @Override
    public String getName() {
        return classPath;
    }

    @Override
    public boolean isTraceEnabled() {
        return isTraceEnabled;
    }

    @Override
    public void trace(final String msg) {
        if (isTraceEnabled()) {
            logMessageWithFormat(TRACE, msg);
        }
    }

    @Override
    public void trace(final String format, final Object arg1) {
        if (isTraceEnabled()) {
            logMessageWithFormat(TRACE, format, arg1);
        }
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        logMessageWithFormat(TRACE, format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        logMessageWithFormat(TRACE, format, arguments);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        log(TRACE, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public void debug(final String msg) {
        logMessageWithFormat(DEBUG, msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logMessageWithFormat(DEBUG, format, arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        logMessageWithFormat(DEBUG, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... args) {
        logMessageWithFormat(DEBUG, format, args);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        log(DEBUG, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    @Override
    public void info(final String msg) {
        logMessageWithFormat(INFO, msg);
    }

    @Override
    public void info(String format, Object arg) {
        logMessageWithFormat(INFO, format, arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        logMessageWithFormat(INFO, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... args) {
        logMessageWithFormat(INFO, format, args);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        log(INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    @Override
    public void warn(final String msg) {
        logMessageWithFormat(WARN, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logMessageWithFormat(WARN, format, arg);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        logMessageWithFormat(WARN, format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... args) {
        logMessageWithFormat(WARN, format, args);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        log(WARN, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

    @Override
    public void error(String format, Object arg) {
        logMessageWithFormat(ERROR, format, arg);
    }

    @Override
    public void error(final String msg) {
        logMessageWithFormat(ERROR, msg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        logMessageWithFormat(ERROR, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... args) {
        logMessageWithFormat(ERROR, format, args);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        log(ERROR, msg, t);
    }

    private void logMessageWithFormat(String levelName, String format, Object... arguments) {
        FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(levelName, tp.getMessage(), tp.getThrowable());
    }

    private void log(String levelName, String message, Throwable t) {
        String dateTime = getFormattedDate();
        String threadName = Thread.currentThread().getName();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(dateTime)
                .append(OPEN_BRACKET)
                .append(threadName)
                .append(CLOSE_BRACKET)
                .append(OPEN_BRACKET)
                .append(levelName)
                .append(CLOSE_BRACKET)
                .append(WHITESPACE)
                .append(classPath)
                .append(HYPHEN)
                .append(message)
                .append(System.lineSeparator());

        writeWithThrowable(stringBuilder, t);
    }

    private String getFormattedDate() {
        Date now = new Date();
        return DATE_FORMAT.format(now);
    }

    void writeWithThrowable(StringBuilder stringBuilder, Throwable t) {
        if (t != null) {
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                t.printStackTrace(pw);
                stringBuilder.append(sw.toString());
            }
        }
        System.out.print(stringBuilder.toString());
    }

    private String loadFromEnvironment(String name) {
        return !StringUtils.isEmpty(System.getenv(name)) ? System.getenv(name) : System.getProperty(name);
    }
}
