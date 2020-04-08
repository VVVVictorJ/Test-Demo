package com.victor.test_demo.UI.Second;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.victor.test_demo.R;
import com.victor.test_demo.UI.BaseFragment;
import com.victor.test_demo.aspectj.annotation.LoginFilter;
import com.victor.test_demo.utils.SharePreference.SharePreferenceUtil;
import com.victor.test_demo.utils.modules.LoginActivity;

import java.util.logging.Handler;

public class UserProfileFragment extends BaseFragment {
    @Override
    protected int getLayoutResId() {
        return R.layout.blank_fragment2_fragment2;
    }

    @Override
    protected void initView(){
        super.initView();
        ImageView imageView = findViewById(R.id.imageView1);
        imageView.setImageResource(R.mipmap.find);
        Button button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @LoginFilter()
            @Override
            public void onClick(View view) {
                SharePreferenceUtil.setBooleanSp(SharePreferenceUtil.IS_LOGIN,
                        false, getActivity());
                SharePreferenceUtil.setUser(SharePreferenceUtil.USER_NAME,
                        "",getActivity());
            }
        });
        Glide.with(this).load("http://192.168.3.107/image/1.png").fitCenter().into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
    }

    @Override
    protected boolean isLazyLoad() {
        return false;
    }


}
