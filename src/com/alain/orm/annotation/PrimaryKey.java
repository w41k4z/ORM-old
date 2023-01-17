package com.alain.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
    Column column();
    String prefix() default ""; // for primaryKey maker 
    int length() default 0; // for primaryKey maker
    String sequenceGetter() default ""; // for primaryKey maker
}