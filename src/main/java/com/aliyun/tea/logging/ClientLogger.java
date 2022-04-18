package com.aliyun.tea.logging;

import com.aliyun.tea.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class ClientLogger {
    private static final Pattern CRLF_PATTERN = Pattern.compile("[\r\n]");
    private final Logger logger;

    public ClientLogger(Class<?> clazz) {
        this(clazz.getName());
    }

    public ClientLogger(String className) {
        Logger initLogger = LoggerFactory.getLogger(className);
        logger = initLogger instanceof NOPLogger ? new DefaultLogger(className) : initLogger;
    }

    public void verbose(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(sanitizeLogMessageInput(message));
        }
    }

    public void verbose(String format, Object... args) {
        if (logger.isDebugEnabled()) {
            performLogging(LogLevel.VERBOSE, false, format, args);
        }
    }

    public void info(String message) {
        if (logger.isInfoEnabled()) {
            logger.info(sanitizeLogMessageInput(message));
        }
    }

    public void info(String format, Object... args) {
        if (logger.isInfoEnabled()) {
            performLogging(LogLevel.INFORMATIONAL, false, format, args);
        }
    }

    public void warning(String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(sanitizeLogMessageInput(message));
        }
    }

    public void warning(String format, Object... args) {
        if (logger.isWarnEnabled()) {
            performLogging(LogLevel.WARNING, false, format, args);
        }
    }

    public void error(String message) {
        if (logger.isErrorEnabled()) {
            logger.error(sanitizeLogMessageInput(message));
        }
    }

    public void error(String format, Object... args) {
        if (logger.isErrorEnabled()) {
            performLogging(LogLevel.ERROR, false, format, args);
        }
    }

    public RuntimeException logExceptionAsWarning(RuntimeException runtimeException) {
        Objects.requireNonNull(runtimeException, "'runtimeException' cannot be null.");
        return logThrowableAsWarning(runtimeException);
    }

    public <T extends Throwable> T logThrowableAsWarning(T throwable) {
        Objects.requireNonNull(throwable, "'throwable' cannot be null.");
        if (!logger.isWarnEnabled()) {
            return throwable;
        }

        performLogging(LogLevel.WARNING, true, throwable.getMessage(), throwable);
        return throwable;
    }

    public RuntimeException logExceptionAsError(RuntimeException runtimeException) {
        Objects.requireNonNull(runtimeException, "'runtimeException' cannot be null.");

        return logThrowableAsError(runtimeException);
    }

    public <T extends Throwable> T logThrowableAsError(T throwable) {
        Objects.requireNonNull(throwable, "'throwable' cannot be null.");
        if (!logger.isErrorEnabled()) {
            return throwable;
        }

        performLogging(LogLevel.ERROR, true, throwable.getMessage(), throwable);
        return throwable;
    }

    private void performLogging(LogLevel logLevel, boolean isExceptionLogging, String format, Object... args) {
        String throwableMessage = "";
        if (doesArgsHaveThrowable(args)) {
            if (!isExceptionLogging) {
                Object throwable = args[args.length - 1];
                if (throwable instanceof Throwable) {
                    throwableMessage = ((Throwable) throwable).getMessage();
                }
            }
            if (!logger.isDebugEnabled()) {
                args = removeThrowable(args);
            }
        }

        sanitizeLogMessageInput(format);
        switch (logLevel) {
            case VERBOSE:
                logger.debug(format, args);
                break;
            case INFORMATIONAL:
                logger.info(format, args);
                break;
            case WARNING:
                if (!StringUtils.isEmpty(throwableMessage)) {
                    format += System.lineSeparator() + throwableMessage;
                }
                logger.warn(format, args);
                break;
            case ERROR:
                if (!StringUtils.isEmpty(throwableMessage)) {
                    format += System.lineSeparator() + throwableMessage;
                }
                logger.error(format, args);
                break;
            default:
                // Don't do anything, this state shouldn't be possible.
                break;
        }

    }

    public boolean canLogAtLevel(LogLevel logLevel) {
        if (logLevel == null) {
            return false;
        }
        switch (logLevel) {
            case VERBOSE:
                return logger.isDebugEnabled();
            case INFORMATIONAL:
                return logger.isInfoEnabled();
            case WARNING:
                return logger.isWarnEnabled();
            case ERROR:
                return logger.isErrorEnabled();
            default:
                return false;
        }
    }

    private boolean doesArgsHaveThrowable(Object... args) {
        if (args.length == 0) {
            return false;
        }

        return args[args.length - 1] instanceof Throwable;
    }

    private Object[] removeThrowable(Object... args) {
        return Arrays.copyOf(args, args.length - 1);
    }

    private static String sanitizeLogMessageInput(String logMessage) {
        if (StringUtils.isEmpty(logMessage)) {
            return logMessage;
        }
        return CRLF_PATTERN.matcher(logMessage).replaceAll("");
    }
}
