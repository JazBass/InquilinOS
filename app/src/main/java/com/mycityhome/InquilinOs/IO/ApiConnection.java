package com.mycityhome.InquilinOs.IO;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiConnection {

    private void turnOffLight() {
        String http = "https://tcpmch.herokuapp.com/?client=Oficina&cmd=toggleLight";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(http)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res = response.body().string();
                    if (res.compareTo("{\"code\":0}") == 0) {
                        res = "Interruptor luz";
                    }
                    Log.d("OKHTTP", "onResponse: " + res);
                    Intent intent = new Intent();
                    intent.putExtra("result", res);
                }
            }
        });
    }




}
