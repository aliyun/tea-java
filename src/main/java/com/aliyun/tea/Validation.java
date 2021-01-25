package com.aliyun.tea;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Validation {
    String pattern() default "";

    int maxLength() default 0;

    int minLength() default 0;

    double maximum() default Double.MAX_VALUE;

    double minimum() default Double.MIN_VALUE;

    boolean required() default false;
}
