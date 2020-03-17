package com.victor.test_demo.aspectj.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
* @Description 定义注解
* @Time 2020-3-17 18：21
* */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginFilter {
    int userDefine() default 0;
}
