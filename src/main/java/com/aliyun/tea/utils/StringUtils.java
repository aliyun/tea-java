package com.aliyun.tea.utils;

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
}
