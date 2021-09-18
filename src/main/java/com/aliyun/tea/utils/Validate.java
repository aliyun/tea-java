package com.aliyun.tea.utils;

public final class Validate {

    private Validate() {
    }

    public static void isTrue(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T> T notNull(final T object, final String message, final Object... values) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return object;
    }

    public static <T> void isNull(final T object, final String message, final Object... values) {
        if (object != null) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T> Class<? extends T> isAssignableFrom(final Class<T> superType, final Class<?> type,
                                                          final String message, final Object... values) {
        if (!superType.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format(message, values));
        }

        return (Class<? extends T>) type;
    }
}
