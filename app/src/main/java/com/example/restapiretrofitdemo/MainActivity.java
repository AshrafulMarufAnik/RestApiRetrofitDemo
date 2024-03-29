package com.example.restapiretrofitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button submitBTN;
    private FloatingActionButton FABtnVolleyParse;
    private EditText userIdET, nameET, titleET, bodyET;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> users;
    private RetrofitInterface retrofitInterface;
    private RequestQueue requestQueue;
    private NetworkCheck networkCheck = new NetworkCheck();
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private final String GET_TAG = "API GET";
    private final String POST_TAG = "API POST";
    private final int RETRY_TIMEOUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //retryHandler();
        //getDataFromApi();
        //showDataFromLocalDB();
        requestQueue = Volley.newRequestQueue(this);
        final boolean activeNetwork = isConnectedToNetwork();

        if (activeNetwork) {
            jsonParseUsingVolley();
        } else {
            swipeRefreshLayout.setRefreshing(true);
            showDataFromLocalDBTable2();
        }

        FABtnVolleyParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParseUsingVolley();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jsonParseUsingVolley();
            }
        });

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userIdET.getText().toString() != null && nameET.getText().toString() != null
                        && titleET.getText().toString() != null && bodyET.getText().toString() != null) {

                    String userId = userIdET.getText().toString();
                    String name = nameET.getText().toString();
                    String title = titleET.getText().toString();
                    String body = bodyET.getText().toString();
                    String status = "Not Synced";

                    storeDataIntoLocalDB(userId, name, title, body, status);
                    try {
                        postDataUsingVolley(userId, name, title, body, status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Fill up all inputs", Toast.LENGTH_SHORT).show();
                }

                userIdET.getText().clear();
                nameET.getText().clear();
                titleET.getText().clear();
                bodyET.getText().clear();
            }
        });
    }

    private void postDataUsingVolley(String userId, String name, String title, String body, String status) throws JSONException {

        String POST_URL = "http://192.168.11.216:80/api/user/write.php";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("userId", userId);
        jsonBody.put("name", name);
        jsonBody.put("title", title);
        jsonBody.put("body", body);
        jsonBody.put("status", status);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, POST_URL, jsonBody,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "POST: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("POST status: ",response.toString());

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "POST Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    private void jsonParseUsingVolley() {
        users.clear();
        String GET_URL = "http://192.168.11.216:80/api/user/read.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_URL, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("users");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String userId = jsonObject.getString("userId");
                        String name = jsonObject.getString("name");
                        String title = jsonObject.getString("title");
                        String body = jsonObject.getString("body");
                        String status = jsonObject.getString("status");

                        User user = new User(userId, name, title, body, status);
                        users.add(user);
                        databaseHelper.insertDataIntoTable2(userId,name,title,body,status);
                    }
                    adapter = new UserAdapter(users,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Data parsed from Api", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void retryHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean activeNetwork = isConnectedToNetwork();

                if (activeNetwork) {
                    //getDataFromApi();
                } else {
                    //swipeRefreshLayout.setRefreshing(true);
                    //showDataFromLocalDB();
                }
            }
        }, RETRY_TIMEOUT);
    }

    private void showDataFromLocalDB() {
        users.clear();
        Cursor currentCursor = databaseHelper.showDataFromLocalDB();

        while (currentCursor.moveToNext()) {
            String userId = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_userId));
            String name = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_name));
            String title = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_title));
            String body = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_body));
            String status = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_status));

            User currentUser = new User(userId, name, title, body, status);
            users.add(currentUser);
        }
        adapter = new UserAdapter(users,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showDataFromLocalDBTable2() {
        users.clear();
        Cursor currentCursor = databaseHelper.showALLDataTable2();

        while (currentCursor.moveToNext()) {
            String userId = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_userId));
            String name = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_name));
            String title = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_title));
            String body = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_body));
            String status = currentCursor.getString(currentCursor.getColumnIndex(databaseHelper.TABLE2_COL_status));

            User currentUser = new User(userId, name, title, body, status);
            users.add(currentUser);
        }
        adapter = new UserAdapter(users,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void storeDataIntoLocalDB(String userId, String name, String title, String body, String status) {
        databaseHelper.insertDataIntoLocalDB(userId, name, title, body, status);
        Toast.makeText(this, "Data stored", Toast.LENGTH_SHORT).show();
    }


    private void getDataFromApi() {
        users.clear();
        retrofitInterface = new ApiClient().getInstance().getApi();

        Call<List<User>> call = retrofitInterface.getData();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    users = response.body();

                    //progressBar.setVisibility(View.GONE);
                    //adapter = new UserAdapter(users);
                    //recyclerView.setAdapter(adapter);
                    Toast.makeText(MainActivity.this, "Data Parsed", Toast.LENGTH_SHORT).show();
                    Log.d(GET_TAG, "API Get successful");

                    int listSize = users.size();
                    for (int i = 0; i < listSize; i++) {
                        User currentUser = users.get(i);
                        databaseHelper.insertDataIntoTable2(currentUser.getUserId(), currentUser.getName(), currentUser.getTitle(), currentUser.getBody(), currentUser.getStatus());

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Data not parsed", Toast.LENGTH_LONG).show();
                    Log.d(GET_TAG, "API GET not successful");
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(GET_TAG, t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkCheck, intentFilter);

        Intent intent = new Intent(MainActivity.this, AppService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(networkCheck);

        Intent intent = new Intent(MainActivity.this, AppService.class);
        stopService(intent);
    }

    private void init() {
        FABtnVolleyParse = findViewById(R.id.FABtn2);
        submitBTN = findViewById(R.id.SubmitBTN);
        userIdET = findViewById(R.id.inputUserIdET);
        nameET = findViewById(R.id.inputNameET);
        titleET = findViewById(R.id.inputTitleET);
        bodyET = findViewById(R.id.inputBodyET);
        swipeRefreshLayout = findViewById(R.id.swipeRef1);
        recyclerView = findViewById(R.id.userRecyclearview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        users = new ArrayList<>();
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public void secondActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }
}
