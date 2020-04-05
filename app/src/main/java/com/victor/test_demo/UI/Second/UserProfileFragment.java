package com.victor.test_demo.UI.Second;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.victor.test_demo.R;
import com.victor.test_demo.UI.BaseFragment;

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
        Glide.with(this).load("http://192.168.3.108/image/1.png").fitCenter().into(imageView);
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
