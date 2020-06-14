package com.example.retrofitdemo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @POST("/v3/weather/weatherInfo")
    @FormUrlEncoded
    Call<ResponseBody> postWeather(@Field("city") String city, @Field("key") String key);
}
