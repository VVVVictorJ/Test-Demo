package com.victor.test_demo.utils.modules;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.victor.test_demo.MainActivity;
import com.victor.test_demo.R;
import com.victor.test_demo.utils.SharePreference.SharePreferenceUtil;
import com.victor.test_demo.utils.encrypt.encryption;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {

    static final String REGISTER = "http://192.168.3.107:5000/register";
    EditText username;
    EditText password;
    EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
    }

    public void RRegister(View view) throws Exception {
        String susername = username.getText().toString();
        String spassword = password.getText().toString();
        String semail = email.getText().toString();
        if (susername.equals("") || spassword.equals("") || semail.equals("")) {
            showWarnSweetDialog("任意一项不能为空");
            return;
        }
        new register_module(REGISTER).run(susername, spassword, semail);
    }

    /*
     *@author victor
     *@Time 2020-3-16 22：16
     * @class 注册模块
     *  */
    public class register_module {

        private String URL;//服务器接口地址
        private final OkHttpClient client = new OkHttpClient();

        public register_module(String URL) {
            this.URL = URL;
        }

        /*
         * @param name 用户名
         * @param passwd 密码
         * @param mail 邮箱
         * */
        public void run(String name, String passwd, String mail) throws Exception {

            final RequestBody requestBody = new FormBody.Builder()
                    .add("username", name)
                    .add("password_hash", new encryption(passwd).convert())
                    .add("email", mail)
                    .build();

            final Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWarnSweetDialog("服务器错误");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessSweetDialog(res);
                            SharePreferenceUtil.setBooleanSp(SharePreferenceUtil.IS_LOGIN,
                                    true, RegisterActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    RegisterActivity.this.finish();
                                }
                            }, 3000);
                        }
                    });
                }
            });
        }

    }

    /*
     * @Description Dialog
     * */
    private void showWarnSweetDialog(String info) {
        SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    /*
     * @Description Dialog
     * */
    private void showSuccessSweetDialog(String info) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
            }
        });
    }
}
