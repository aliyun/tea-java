package com.aliyun.tea.utils;

public class IOUtils {

    private IOUtils() {
    }

    public static void closeQuietly(AutoCloseable is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    public static void closeIfCloseable(Object maybeCloseable) {
        if (maybeCloseable instanceof AutoCloseable) {
            closeQuietly((AutoCloseable) maybeCloseable);
        }
    }
}
