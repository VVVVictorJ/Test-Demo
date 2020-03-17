package com.victor.test_demo.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.victor.test_demo.MainActivity;
import com.victor.test_demo.R;
import com.victor.test_demo.utils.encrypt.encryption;
import com.victor.test_demo.utils.modules.login_module;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//TODO ：splash activity 之后跳转到 登录界面 -2020.3.16
//TODO ：登录成功 之后跳转到 主界面 -2020.3.16
public class LoginActivity extends Activity {
    static final String LOGIN = "http://127.0.0.1/login";

    EditText username;
    EditText password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        username = findViewById(R.id.eusername);
        password = findViewById(R.id.passwd_text);

    }

    /*
    * @Description 登录模块
    * */
    public void Login(View view) {
        String sUsername = username.getText().toString();
        String sPasswd =password.getText().toString();

        //TODO 调用登录模块 login_module
        if(sUsername.equals("")||sPasswd.equals("")){
            showWarnSweetDialog("账号密码不能为空");
            return;
        }
        new login_module(LOGIN).run(sUsername, sPasswd);
    }

    public void Register(View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                LoginActivity.this.finish();
            }
        });
    }

    private void showWarnSweetDialog(String info)
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    private void showSuccessSweetDialog(String info)
    {
        final SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
            {
                pDialog.dismiss();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                });
            }
        });
    }

    //Warning 可能会有bug ，这是内部类。
    protected class login_module {
        private String URL;//服务器接口地址
        private final OkHttpClient client = new OkHttpClient();//构造client单例

        public login_module(String URL) {
            this.URL = URL;
        }

        /*
         * @param name 用户名
         * @param passwd 密码
         * @method 异步发送
         * */
        public void run(String name, String passwd) {
            final RequestBody requestBody = new FormBody.Builder()
                    .add("username", name)
                    .add("password_hash", new encryption(passwd).convert())
                    .build();
            final Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showWarnSweetDialog("服务器错误");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (res.equals("0"))
                            {
                                showWarnSweetDialog("无此账号,请先注册");
                            }
                            else if(res.equals("1"))
                            {
                                showWarnSweetDialog("密码不正确");
                            }
                            else//成功
                            {
                                showSuccessSweetDialog(res);
                                //TODO SharePrefences 记录
                            }

                        }
                    });

                }
            });


        }
    }
}
