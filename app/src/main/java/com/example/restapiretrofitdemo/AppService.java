package com.example.restapiretrofitdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppService extends Service {
    private static final String TAG = ""+MyAsync.class.getName();
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private RetrofitInterface retrofitInterface;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: "+Thread.currentThread().getName());

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand: "+Thread.currentThread().getName());

        new MyAsync();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind"+Thread.currentThread().getName());
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy"+Thread.currentThread().getName());
        super.onDestroy();
    }

    class MyAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean activeNetwork = isConnectedToNetwork();

            if(activeNetwork)
            {
                final Cursor cursor = databaseHelper.showUnSyncedData();
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_userId));
                    String name = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_name));
                    String title = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_title));
                    String body = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_body));
                    String status = "Synced";

                    retrofitInterface = new ApiClient().getInstance().getApi();

                    Call<List<User>> call = retrofitInterface.postData(id,name,title,body,status);
                    call.enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(AppService.this, "Data Synced", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(AppService.this, "Data could not sync", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            Toast.makeText(AppService.this, "API POST Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
