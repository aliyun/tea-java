package com.aliyun.tea.utils;

import java.util.Objects;

public class StringUtils {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isEmpty(Object object) {
        if (null != object) {
            return isEmpty(String.valueOf(object));
        }
        return true;
    }

    public static String join(String delimiter, Iterable<? extends String> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : elements) {
            stringBuilder.append(value);
            stringBuilder.append(delimiter);
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
