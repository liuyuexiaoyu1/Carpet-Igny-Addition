package com.liuyue.igny.logging.annotation;

import com.liuyue.igny.logging.callback.LoggerCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObserveLogger {
    Class<? extends LoggerCallback> value();
}
