package com.victor.test_demo.splash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Window;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.victor.test_demo.MainActivity;
import com.victor.test_demo.utils.LoginActivity;

import com.victor.test_demo.R;

import androidx.annotation.Nullable;


/*
 * @author: victor
 * @Time: 2020-3-16 17:43
 * @activity 入场图片展示
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS); //开启动画的特征
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);//去标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setExitTransition(new Fade());
        getWindow().setEnterTransition(new Explode());

        setContentView(R.layout.activity_splash);
        /*
        * 使用第三方库加载图片，imageview 无法加载过大的图片
        */
        ImageView imageView = findViewById(R.id.imageView);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        imageLoader.displayImage("drawable://" + R.drawable.nstar, imageView);

        final int SPLASH_DISPLAY_LENGTH = 3000;                //延迟时长 5s

        /*
        * handler 线程安全处理
        */
        new Handler().postDelayed(new Runnable() {          //本质理解是Handler延迟调用线程跳转activity
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this).toBundle());                      //跳转到登录界面
                SplashActivity.this.finish();               //结束该activity
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
