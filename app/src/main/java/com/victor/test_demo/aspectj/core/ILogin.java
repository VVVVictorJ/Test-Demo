package com.victor.test_demo.aspectj.core;

import android.content.Context;

public interface ILogin {

    void login(Context context, int userDefine);

    boolean isLogin(Context applicationContext);

    void clearLoginStatus(Context applicationContext);
}
