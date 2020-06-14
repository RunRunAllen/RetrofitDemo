package com.example.retrofitdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

    }

    private void initData() {
        //自定义 简单Retrofit
        CustomRetroift retroift = new CustomRetroift.Builder().baseUrl("httpurl").build();
        Api api = retroift.create(Api.class);
        Call<ResponseBody> call = api.postWeather("", "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
