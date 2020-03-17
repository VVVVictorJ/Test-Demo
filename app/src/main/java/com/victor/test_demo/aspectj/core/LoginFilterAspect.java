package com.victor.test_demo.aspectj.core;

import android.content.Context;

import com.victor.test_demo.aspectj.annotation.LoginFilter;
import com.victor.test_demo.aspectj.expection.AnnotationException;
import com.victor.test_demo.aspectj.expection.NoInitException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/*
* @description 注入点方法
* */
@Aspect
public class LoginFilterAspect {
    private static final String TAG = "LoginFilterAspect";

    @Pointcut("execution(@com.victor.test_demo.aspectj.annotation.LoginFilter * *(..))")
    public void loginFilter(){}

    @Around("loginFilter()")
    public void aroundLoginPoint(ProceedingJoinPoint joinPoint)throws Throwable{

        ILogin iLogin = LoginAssistant.getInstance().getiLogin();
        if (iLogin==null){
            throw new NoInitException("LoginSDK没有变化");
        }

        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)){
            throw new AnnotationException("LoginFilter 注解只能用于方法上");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        LoginFilter loginFilter = methodSignature.getMethod().getAnnotation(LoginFilter.class);
        if (loginFilter == null) {
            return;
        }

        Context param = LoginAssistant.getInstance().getApplicationContext();
        if (iLogin.isLogin(param)){
            joinPoint.proceed();//如果登陆了，则执行原有方法。
        }else {
            iLogin.login(param,loginFilter.userDefine());//执行跳转至登录界面
        }
    }
}
