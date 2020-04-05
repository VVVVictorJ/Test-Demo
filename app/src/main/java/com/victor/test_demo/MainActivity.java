package com.victor.test_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.next.easynavigation.view.EasyNavigationBar;
import com.victor.test_demo.UI.First.RecyclerViewFragment;
import com.victor.test_demo.UI.Second.UserProfileFragment;
import com.victor.test_demo.aspectj.annotation.LoginFilter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private EasyNavigationBar easyNavigationBar;

    private String[] tabText = {"首页","发布","我的"};

    private int[] normalIcon = {R.mipmap.index, R.mipmap.add_image, R.mipmap.me};

    private int[] selectIcon = {R.mipmap.index1, R.mipmap.add_image, R.mipmap.me1};

    private List<Fragment> fragments = new ArrayList<>();

    private LinearLayout menuLayout;
    private View cancelImageView;
    private Handler mHandler = new Handler();

    public static int mCurrentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //底部导航栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easyNavigationBar = findViewById(R.id.navigationBar);
        fragments.add(new RecyclerViewFragment());
        fragments.add(new UserProfileFragment());


        easyNavigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .addLayoutRule(EasyNavigationBar.RULE_BOTTOM)
                .addLayoutBottom(200)
                .onTabClickListener(new EasyNavigationBar.OnTabClickListener() {

                    @Override
                    public boolean onTabClickEvent(View view, int position) {
                        if (position == 1){
                            //TODO 注解
                            Intent intent = new Intent(MainActivity.this, ConvertActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                })
                .mode(EasyNavigationBar.MODE_ADD)//突出模式
                .build();
    }

    @LoginFilter
    public void test_if_loaded(View view) {
        //TODO 此方法是应用于tab切换上的，若没有登录，
        // 则跳转至登陆界面，反之跳转至点击tab 2020-3-17 18：18
    }
}
