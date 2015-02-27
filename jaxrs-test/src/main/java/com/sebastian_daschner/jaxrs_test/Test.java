package com.sebastian_daschner.jaxrs_test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Test {

    public static final String STATIC_STRING = "123";

    String value() default "value";

    int test();

}
