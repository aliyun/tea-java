package com.aliyun.tea.logging;

import java.util.HashMap;
import java.util.Locale;

public enum LogLevel {

    VERBOSE(1, "1", "verbose", "debug"),

    INFORMATIONAL(2, "2", "info", "information", "informational"),

    WARNING(3, "3", "warn", "warning"),

    ERROR(4, "4", "err", "error"),

    NOT_SET(5, "5");

    private final int numericValue;
    private final String[] allowedLogLevelVariables;
    private static final HashMap<String, LogLevel> LOG_LEVEL_STRING_MAPPER = new HashMap<>();

    static {
        for (LogLevel logLevel : LogLevel.values()) {
            for (String val : logLevel.allowedLogLevelVariables) {
                LOG_LEVEL_STRING_MAPPER.put(val, logLevel);
            }
        }
    }

    LogLevel(int numericValue, String... allowedLogLevelVariables) {
        this.numericValue = numericValue;
        this.allowedLogLevelVariables = allowedLogLevelVariables;
    }

    public int getLogLevel() {
        return numericValue;
    }

    public static LogLevel fromString(String logLevelVal) {
        if (logLevelVal == null) {
            return LogLevel.NOT_SET;
        }
        String caseInsensitiveLogLevel = logLevelVal.toLowerCase(Locale.ROOT);
        if (!LOG_LEVEL_STRING_MAPPER.containsKey(caseInsensitiveLogLevel)) {
            throw new IllegalArgumentException("We currently do not support the log level you set. LogLevel: "
                    + logLevelVal);
        }
        return LOG_LEVEL_STRING_MAPPER.get(caseInsensitiveLogLevel);
    }
}
