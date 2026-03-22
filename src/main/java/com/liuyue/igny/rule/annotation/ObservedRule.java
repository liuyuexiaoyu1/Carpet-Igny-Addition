package com.liuyue.igny.rule.annotation;

import com.liuyue.igny.rule.RuleCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObservedRule {
    Class<? extends RuleCallback<?>> callback();
}
