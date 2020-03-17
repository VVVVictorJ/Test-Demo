package com.victor.test_demo.utils.modules;
import android.content.Intent;

import com.victor.test_demo.MainActivity;
import com.victor.test_demo.utils.LoginActivity;
import com.victor.test_demo.utils.encrypt.encryption;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
* @author victor
* @Time 2020-3-16 21:42
* */
public class login_module {
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
                System.out.println(e.getMessage());//TODO 使用showmessage
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(Objects.requireNonNull(response.body()).string());
                //TODO sharepreference 标识已经登录
            }
        });


    }
}
