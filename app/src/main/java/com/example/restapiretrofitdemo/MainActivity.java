package com.example.restapiretrofitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button submitBTN;
    private EditText userIdET, nameET, titleET, bodyET;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> users;
    private RetrofitInterface retrofitInterface;
    private ProgressBar progressBar;
    private NetworkCheck networkCheck = new NetworkCheck();
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private final String GET_TAG = "API GET";
    private final String POST_TAG = "API POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //getData();

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userIdET.getText().toString()!= null && nameET.getText().toString()!= null
                    && titleET.getText().toString()!= null && bodyET.getText().toString()!= null){

                    String userId = userIdET.getText().toString();
                    String name = nameET.getText().toString();
                    String title = titleET.getText().toString();
                    String body = bodyET.getText().toString();

                    storeDataIntoLocalDB(userId,name,title,body);
                }
                else {
                    Toast.makeText(MainActivity.this, "Fill up all inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeDataIntoLocalDB(String userId, String name, String title, String body) {
        databaseHelper.insertDataIntoLocalDB(userId,name,title,body,"Not Synced");
    }


    private void getData() {
        users.clear();
        retrofitInterface = new ApiClient().getInstance().getApi();

        Call<List<User>> call = retrofitInterface.getData();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.body()!=null){
                    users = response.body();
                    //progressBar.setVisibility(View.GONE);
                    //adapter = new UserAdapter(users);
                    //recyclerView.setAdapter(adapter);
                    Toast.makeText(MainActivity.this, "Data Parsed", Toast.LENGTH_SHORT).show();
                    Log.d(GET_TAG,"API Get successful");

                    int listSize = users.size();
                    for(int i=0;i<listSize;i++){
                        User currentUser = users.get(i);
                        databaseHelper.insertDataIntoTable2(currentUser.getUserId(),currentUser.getName(),currentUser.getTitle(),currentUser.getBody(),currentUser.getStatus());
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Data not parsed", Toast.LENGTH_LONG).show();
                    Log.d(GET_TAG,"API GET not successful");
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(GET_TAG,t.getMessage());
            }
        });

    }

    public void showData(){


    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkCheck,intentFilter);

        //Intent intent = new Intent(MainActivity.this,AppService.class);
        //startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean activeNetwork = isConnectedToNetwork();

        if(activeNetwork){
            getData();

            Cursor cursor1 =  databaseHelper.showUnSyncedData(); //returns all not-synced data from local db

            while(cursor1.moveToNext()){
                String userId = String.valueOf(cursor1.getColumnIndex(databaseHelper.COL_userId));
                String name = String.valueOf(cursor1.getColumnIndex(databaseHelper.COL_name));
                String title = String.valueOf(cursor1.getColumnIndex(databaseHelper.COL_title));
                String body = String.valueOf(cursor1.getColumnIndex(databaseHelper.COL_body));
                String status = String.valueOf(cursor1.getColumnIndex(databaseHelper.COL_status));

                postDataToApi(userId,name,title,body,status);
            }
        }
        else {
            Cursor currentCursor = databaseHelper.showALLDataTable2();

            while (currentCursor.moveToNext()){
                String userId = String.valueOf(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_userId));
                String name = String.valueOf(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_name));
                String title = String.valueOf(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_title));
                String body = String.valueOf(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_body));
                String status = String.valueOf(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_status));

                User currentUser = new User(userId,name,title,body,status);
                users.add(currentUser);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void postDataToApi(String userId, String name, String title, String body, String status) {
        retrofitInterface = new ApiClient().getInstance().getApi();

        Call<List<User>> call = retrofitInterface.postData(userId,name,title,body,"Synced");
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Data Synced", Toast.LENGTH_SHORT).show();
                    Log.d(POST_TAG, "API POST Success");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                Log.d(POST_TAG,"API POST unsuccessful");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(networkCheck);

        //Intent intent = new Intent(MainActivity.this,AppService.class);
        //stopService(intent);
    }

    private void init() {
        recyclerView = findViewById(R.id.userRecyclearview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        users = new ArrayList<>();
        submitBTN = findViewById(R.id.SubmitBTN);
        userIdET = findViewById(R.id.inputUserIdET);
        nameET = findViewById(R.id.inputNameET);
        titleET = findViewById(R.id.inputTitleET);
        bodyET = findViewById(R.id.inputBodyET);

        adapter = new UserAdapter(users);
        recyclerView.setAdapter(adapter);

    }

    private boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
