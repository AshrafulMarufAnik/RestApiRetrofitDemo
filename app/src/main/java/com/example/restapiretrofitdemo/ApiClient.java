package com.example.restapiretrofitdemo;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

public class ApiClient {
    //private static String BASE_URL="http://localhost/api/user/";
    private static String BASE_URL="http://192.168.11.216/api/users/";
    private static ApiClient mInstance;
    private Retrofit retrofit;


    public ApiClient(){

        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
        }
        catch (MalformedURLException e) {
            Log.i("URL Conn",e.getMessage());
        }
        catch (IOException e) {
            Log.i("URL Conn",e.getMessage());
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ApiClient getInstance(){
        if(mInstance == null){
            mInstance = new ApiClient();
        }
        return mInstance;

    }

    public RetrofitInterface getApi(){
        return retrofit.create(RetrofitInterface.class);
    }

}
