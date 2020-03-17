package com.victor.test_demo.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.victor.test_demo.aspectj.core.ILogin;
import com.victor.test_demo.aspectj.core.LoginSDK;
import com.victor.test_demo.utils.LoginActivity;
import com.victor.test_demo.utils.SharePreference.SharePreferenceUtil;


/*
* @Description 初始化
* */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LoginSDK.getInstance().init(this,iLogin);//通过初始化SDK类初始化各个组件
    }

    ILogin iLogin = new ILogin() {
        @Override
        public void login(Context applicationContext, int userDefine) {
            switch (userDefine) {
                case 0:
                    startActivity(new Intent(applicationContext, LoginActivity.class));//定义未登录行为
                    break;
                case 1:
                    Toast.makeText(applicationContext, "您还没有登录，请登陆后执行", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(applicationContext, "执行失败，因为您还没有登录！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public boolean isLogin(Context applicationContext) {
            return SharePreferenceUtil.getBooleanSp(SharePreferenceUtil.IS_LOGIN, applicationContext);
        }

        @Override
        public void clearLoginStatus(Context applicationContext) {
            SharePreferenceUtil.setBooleanSp(SharePreferenceUtil.IS_LOGIN, false, applicationContext);
        }
    };
}
