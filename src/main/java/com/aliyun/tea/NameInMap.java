package com.aliyun.tea;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NameInMap {

    /**
     * @return the desired name of the field when it is serialized or deserialized
     */
    String value();

    /**
     * @return the alternative names of the field when it is deserialized
     */
    String[] alternate() default {};
}
