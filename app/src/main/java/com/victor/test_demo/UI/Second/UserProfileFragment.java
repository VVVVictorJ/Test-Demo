package com.victor.test_demo.UI.Second;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.victor.test_demo.R;
import com.victor.test_demo.UI.BaseFragment;
import com.victor.test_demo.aspectj.annotation.LoginFilter;
import com.victor.test_demo.utils.SharePreference.SharePreferenceUtil;
import com.victor.test_demo.utils.modules.LoginActivity;
import com.victor.test_demo.utils.modules.encode.base64_module;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vip.upya.lib.sfof.SelectFileOrFolderDialog;

public class UserProfileFragment extends BaseFragment {
    private ImageView imageView;
    private boolean flag = false;
    private boolean isFlag = false;
    private static final String SERVER_URL = "http://192.168.3.107:5000/load/profile";
    private static final String SERVER_URL_UP = "http://192.168.3.107:5000/update/profile";
    private static final String STORAGE_URL = "/storage/emulated/0/A/";
    private String IMG_URL;

    @Override
    protected int getLayoutResId() {
        return R.layout.blank_fragment2_fragment2;
    }

    @Override
    protected void initView(){
        super.initView();
        imageView = findViewById(R.id.imageView1);
        imageView.setImageResource(R.mipmap.find);
        Button button = findViewById(R.id.button3);
        //退出登录
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 选择头像
                new SelectFileOrFolderDialog(getActivity(), true, SelectFileOrFolderDialog.CHOICEMODE_ONLY_FILE,
                        new SelectFileOrFolderDialog.OnSelectFileOrFolderListener() {
                            @Override
                            public void onSelectFileOrFolder(List<File> selectedFileList) {
                                IMG_URL = selectedFileList.get(0).getAbsolutePath();
                            }
                        }).show();
                //TODO 线程发送
                try {
                    new upload_test(SERVER_URL_UP,IMG_URL).run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //TODO 用反射改进
        if (isFlag){
            Glide.with(this)
                    .load(IMG_URL)
                    .transform(new BitmapTransformation() {
                        @Override
                        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                            return TransformationUtils.circleCrop(pool, toTransform,outWidth/3, outHeight/3);
                        }

                        @Override
                        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                        }
                    })
                    .into(imageView);
        }
        if (!flag){
            try {
                new get_test(SERVER_URL, STORAGE_URL).run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (flag){
            String filename = SharePreferenceUtil.getUser(SharePreferenceUtil.USER_NAME,getActivity());
            String path = STORAGE_URL+filename+".jpg";
            Glide.with(this)
                    .load(path)
                    .transform(new BitmapTransformation() {
                        @Override
                        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                            return TransformationUtils.circleCrop(pool, toTransform,outWidth/3, outHeight/3);
                        }

                        @Override
                        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                        }
                    })
                    .into(imageView);
        }
    }

    @Override
    protected boolean isLazyLoad() {
        return false;
    }

    /*
    * @ description 加载头像类
    * @ author Victor
    * @ Time 2020-4-13 11-43
    * */
    public class get_test {
        private String SERVER_URL;
        private String SAVE_URL;
        /*
         * 设置超时时间
         * */
        final static int CONNECT_TIMEOUT = 10000;
        final static int READ_TIMEOUT = 10000;
        final static int WRITE_TIMEOUT = 10000;
        private final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        public get_test(String SERVER_URL, String SAVE_URL) {
            this.SERVER_URL = SERVER_URL;
            this.SAVE_URL = SAVE_URL;
        }

        public void run() throws Exception {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", "victor")
                    .build();
            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String tmp = Objects.requireNonNull(response.body()).string();
                    if (new base64_module().Decode_To_Image(tmp, SAVE_URL,"jpg")) {
                        flag = true;
                    }
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String filename = SharePreferenceUtil.getUser(SharePreferenceUtil.USER_NAME,getActivity());
                            String path = STORAGE_URL+filename+".jpg";
                            Glide.with(getActivity())
                                    .load(path)
                                    .transform(new BitmapTransformation() {
                                        @Override
                                        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                                            return TransformationUtils.circleCrop(pool, toTransform,outWidth/3, outHeight/3);
                                        }

                                        @Override
                                        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                                        }
                                    })
                                    .into(imageView);
                        }
                    });
                }
            });
        }

    }

    /*
     * @ description 上传头像类
     * @ author Victor
     * @ Time 2020-4-13 11-43
     * */
    public class upload_test {
        private String SERVER_URL;
        private String IMG_URL;
        /*
         * 设置超时时间
         * */
        final static int CONNECT_TIMEOUT = 10000;
        final static int READ_TIMEOUT = 10000;
        final static int WRITE_TIMEOUT = 10000;
        private final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        public upload_test(String SERVER_URL, String IMG_URL) {
            this.SERVER_URL = SERVER_URL;
            this.IMG_URL = IMG_URL;
        }

        public void run() throws Exception {
            String b64 = new base64_module(IMG_URL).Encode_To_Base64();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", "victor")//改成sharepreference
                    .addFormDataPart("b64", b64)
                    .addFormDataPart("filename", "victor.jpg")
                    .build();
            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //sweetdialog
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    int flag = Integer.parseInt(Objects.requireNonNull(response.body()).string());
                    if (flag == 0){
                        return;
                    }else {
                        //glide展示
                        //写入本地
                        isFlag = true;
                    }
                }
            });
        }
    }
}
