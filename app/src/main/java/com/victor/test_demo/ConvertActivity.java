package com.victor.test_demo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.victor.test_demo.utils.SharePreference.SharePreferenceUtil;
import com.victor.test_demo.utils.modules.LoginActivity;
import com.victor.test_demo.utils.modules.encode.base64_module;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private TextView textView;
    private Spinner spinner;
    private String ratio;
    private Button button;
    boolean b = true;

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

    //TODO 移植zip模块
    public void upload(View view) throws Exception {
        String get = textView.getText().toString();
        if (get.substring(get.length() - 3, get.length()).equals("DNG")) {
            button.setClickable(false);
            new test_dng(get).run();
            //TODO Gilde 加载略缩图
        } else if (get.substring(get.length() - 3, get.length()).equals("zip")) {
            button.setClickable(false);
            new test__zip(ZIP_URL, get).run();
            //TODO Gilde 加载略缩图
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
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (new base64_module().Decode_To_Image(tmp, "png")) {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (new writeFile().write(SAVE_URL, tmp)){
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
                            }
                        }
                    });

                }
            });
        }

        class writeFile{
            boolean write(String path, byte[] array){
                //TODO 完成写入文件模块
                String random_file_name = UUID.randomUUID().toString();
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(SAVE_URL+random_file_name+".avi"));
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
