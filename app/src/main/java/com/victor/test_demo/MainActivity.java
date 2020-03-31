package com.victor.test_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.victor.test_demo.aspectj.annotation.LoginFilter;

//TODO ：主界面 放 底部导航栏 -2020.3.17

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //底部导航栏
    }

    @LoginFilter
    public void test_if_loaded(View view) {
        //TODO 此方法是应用于tab切换上的，若没有登录，
        // 则跳转至登陆界面，反之跳转至点击tab 2020-3-17 18：18
    }
}
