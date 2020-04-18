package com.victor.test_demo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.cazaea.sweetalert.SweetAlertDialog;
import com.victor.test_demo.utils.SharePreferences.SharePreferenceUtil;
import com.victor.test_demo.utils.module.encode.base64_module;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vip.upya.lib.sfof.SelectFileOrFolderDialog;

public class ConvertActivity extends AppCompatActivity {

    final String ZIP_URL = "http://192.168.3.107:5000/predict/video/";
    static String STORAGE_URL = "/storage/emulated/0/DCIM/";
    static String SAVE_URL = "/storage/emulated/0/DCIM/Video/";
    private TextView textView;
    private Spinner spinner;
    private String ratio;
    private Button button;
    boolean b = true;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        textView = findViewById(R.id.filename);
        button = findViewById(R.id.button);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (b) {
                    view.setVisibility(View.INVISIBLE);//初始时不显示
                } else {
                    ratio = getResources().getStringArray(R.array.ratio)[position];
                }
                b = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ratio = getResources().getStringArray(R.array.ratio)[0];
            }
        });
        imageView = findViewById(R.id.imageView2);
    }

    public void choosefile(View view) {
        new SelectFileOrFolderDialog(this, true, SelectFileOrFolderDialog.CHOICEMODE_ONLY_FILE,
                new SelectFileOrFolderDialog.OnSelectFileOrFolderListener() {
                    @Override
                    public void onSelectFileOrFolder(List<File> selectedFileList) {
                        textView.setText(selectedFileList.get(0).getAbsolutePath());
                    }
                }).show();
    }

    public void upload(View view) throws Exception {
        String get = textView.getText().toString();
        if (get.substring(get.length() - 3, get.length()).equals("DNG")) {
            button.setClickable(false);
            new test_dng(get).run();
        } else if (get.substring(get.length() - 3, get.length()).equals("zip")) {
            button.setClickable(false);
            new test__zip(ZIP_URL, get).run();
        } else {
            Toast.makeText(this, "请选中DNG或ZIP文件", Toast.LENGTH_SHORT).show();
            textView.setText("请选择转换文件");
            return;
        }

    }

    /*
     * @description DNG图片发送类
     * @author victor
     * @Time 2020/4/7 17-44-49
     * @format 内部类
     * */
    public class test_dng {
        final String SERVER_URL = "http://192.168.3.107:5000/predict/image/";
        private final MediaType MEDIA_TYPE_OSTREAM = MediaType.parse("application/octet-stream");
        final static int CONNECT_TIMEOUT = 1000;
        final static int READ_TIMEOUT = 1000;
        final static int WRITE_TIMEOUT = 1000;
        private String IMAGE_URL;

        private OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        public test_dng(String IMAGE_URL) {
            this.IMAGE_URL = IMAGE_URL;
        }

        public void run() throws Exception {
            String send = new base64_module(IMAGE_URL).Encode_To_Base64();
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + ".DNG";
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", SharePreferenceUtil
                            .getUser(SharePreferenceUtil.USER_NAME, ConvertActivity.this))
                    .addFormDataPart("b64", send)
                    .addFormDataPart("ratio", ratio)
                    .addFormDataPart("filename", filename)
//                .addFormDataPart("file","tets.PNG",
//                        RequestBody.create(send,MEDIA_TYPE_OSTREAM))
                    .build();
            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String tmp = Objects.requireNonNull(response.body()).string();
                    String random_file_name = UUID.randomUUID().toString();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // 写入完成后显示通知栏
                            if (new base64_module().Decode_To_Image(tmp, random_file_name, "png")) {
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(
                                        ConvertActivity.this, SweetAlertDialog.WARNING_TYPE
                                );
                                sweetAlertDialog.getProgressHelper()
                                        .setBarColor(Color.parseColor("#A5DC86"));
                                sweetAlertDialog.setTitleText("完成");
                                sweetAlertDialog.setCancelable(true);
                                sweetAlertDialog.show();
                                new CountDownTimer(2000, 1000) {
                                    @Override
                                    public void onTick(long l) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        sweetAlertDialog.dismiss();//自动销毁，防止内存泄漏
                                    }
                                }.start();
                                button.setClickable(true);
                                //加载略缩图
                                Glide.with(ConvertActivity.this)
                                        .load(STORAGE_URL+random_file_name+".png")
                                        .fitCenter()
                                        .into(imageView);
                            }
                        }
                    });

                }
            });
        }
    }

    /*
     * @description ZIP发送类
     * @author victor
     * @Time
     * @format 内部类
     * */
    public class test__zip {
        private String URL ;
        private String FILE_URL;
        String SAVE_URL = "/storage/emulated/0/DCIM/Video/";

        private final MediaType ZIP_FILE_STREAM = MediaType.parse("multipart/form-data");
        final static int CONNECT_TIMEOUT =10000;
        final static int READ_TIMEOUT=10000;
        final static int WRITE_TIMEOUT=10000;
        private final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        public test__zip(String URL, String FILE_URL) {
            this.URL = URL;
            this.FILE_URL = FILE_URL;
        }

        public void run(){
            String uuid = UUID.randomUUID().toString();
            uuid = uuid +".zip";
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id",
                            SharePreferenceUtil.getUser(SharePreferenceUtil.USER_NAME,
                                    ConvertActivity.this))
                    .addFormDataPart("ratio",ratio)
                    .addFormDataPart("file",uuid,
                            RequestBody.create(new File(FILE_URL),ZIP_FILE_STREAM))
                    .build();
            Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //System.out.println(Objects.requireNonNull(response.body()).string());
                    byte[] tmp = Objects.requireNonNull(response.body()).bytes();
                    String random_file_name = UUID.randomUUID().toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (new writeFile().write(SAVE_URL,random_file_name, tmp)){
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(
                                        ConvertActivity.this, SweetAlertDialog.WARNING_TYPE
                                );
                                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                sweetAlertDialog.setTitleText("完成");
                                sweetAlertDialog.setCancelable(true);
                                sweetAlertDialog.show();
                                new CountDownTimer(2000, 1000) {
                                    @Override
                                    public void onTick(long l) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        sweetAlertDialog.dismiss();//自动销毁，防止内存泄漏
                                    }
                                }.start();
                                button.setClickable(true);
                                //加载视频第一帧
                                Glide.with(ConvertActivity.this)
                                        .load(SAVE_URL+random_file_name+".mp4")
                                        .transform(new BitmapTransformation() {
                                            @Override
                                            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                                                return TransformationUtils.rotateImage(Bitmap.createScaledBitmap(
                                                        toTransform, toTransform.getWidth() / 2,
                                                        toTransform.getHeight() / 2, false), 90);
                                            }

                                            @Override
                                            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                                            }
                                        })
                                        .into(imageView);
                            }
                        }
                    });

                }
            });
        }

        class writeFile{
            boolean write(String path, String uuid, byte[] array){
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(SAVE_URL+uuid+".mp4"));
                    outputStream.write(array);
                    outputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return true;
            }
        }
    }

}
