package com.example.restapiretrofitdemo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @GET("read.php")
    Call<List<User>> getData();

    @FormUrlEncoded
    @POST ("write.php")
    Call<List<User>> postData(
            @Field("userId") String userId,
            @Field("name") String name,
            @Field("title") String title,
            @Field("body") String body,
            @Field("status") String status
    );


}
