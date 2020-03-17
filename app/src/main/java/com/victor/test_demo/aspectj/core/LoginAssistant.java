package com.victor.test_demo.aspectj.core;

import android.content.Context;

public class LoginAssistant {
    private LoginAssistant() {}

    private static LoginAssistant instance;


    public static LoginAssistant getInstance(){
        if (instance==null){
            synchronized (LoginAssistant.class){    //class对象锁，只允许每次只有一个线程执行创建对象
                if (instance==null){
                    instance = new LoginAssistant();
                }
            }
        }
        return instance;
    }

    private ILogin iLogin;  //接口对象

    public ILogin getiLogin() {
        return iLogin;
    }

    public void setiLogin(ILogin iLogin) {
        this.iLogin = iLogin;
    }

    private Context applicationContext; //上下文对象

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void serverTokenInvalidation(int userDefine){
        if (iLogin==null)
            return;
        iLogin.clearLoginStatus(applicationContext);
        iLogin.login(applicationContext,userDefine);
    }
}
