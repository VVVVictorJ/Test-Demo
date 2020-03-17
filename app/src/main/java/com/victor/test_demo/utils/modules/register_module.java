package com.victor.test_demo.utils.modules;
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
    public void run(String name, String passwd, String mail) throws Exception{

        final RequestBody requestBody =  new FormBody.Builder()
                .add("username",name)
                .add("password_hash",new encryption(passwd).convert())
                .add("email",mail)
                .build();

        final Request request = new Request.Builder()
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
                System.out.println(Objects.requireNonNull(response.body()).string());
            }
        });
    }

}
