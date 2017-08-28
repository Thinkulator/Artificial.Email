package com.bootseg.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright Eric Kerin 2008 - Licensed under the LGPL
 * @author Eric Kerin <eric@bootseg.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface Column {
    String value();
    boolean isPrimary() default false;
    String cast() default "";
}

