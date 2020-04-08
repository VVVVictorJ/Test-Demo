package com.victor.test_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.next.easynavigation.view.EasyNavigationBar;
import com.victor.test_demo.UI.First.RecyclerViewFragment;
import com.victor.test_demo.UI.Second.UserProfileFragment;
import com.victor.test_demo.aspectj.annotation.LoginFilter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private EasyNavigationBar easyNavigationBar;

    private String[] tabText = {"首页", "发布", "我的"};

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
                .mode(EasyNavigationBar.MODE_ADD)//突出模式
                .build();
        easyNavigationBar.onTabClickListener(new EasyNavigationBar.OnTabClickListener() {
            @Override
            public boolean onTabClickEvent(View view, int position) {
                if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, ConvertActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    @LoginFilter
    public void test_if_loaded(View view) {

    }
}
