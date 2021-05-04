package com.github.devgcoder.devgmethod;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DevgMethod {

  String name() default "";

  String desc() default "";

  int expireSeconds() default 1800;
}
