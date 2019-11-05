package com.aliyun.tea;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Validation {
    String pattern() default "";

    int maxLength() default 0;

    boolean required() default false;
}
