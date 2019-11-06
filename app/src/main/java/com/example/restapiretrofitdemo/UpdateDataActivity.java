package com.example.restapiretrofitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateDataActivity extends AppCompatActivity {
    private EditText updateET1,updateET2,updateET3,updateET4;
    private Button updateBTN;
    private String userId,name,title,body,status;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        init();
        requestQueue = Volley.newRequestQueue(this);

        if(getIntent().getExtras()!=null){
            userId = getIntent().getStringExtra("userId");
            name = getIntent().getStringExtra("name");
            title = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("body");
            status = getIntent().getStringExtra("status");

            updateET1.setText(userId);
            updateET2.setText(name);
            updateET3.setText(title);
            updateET4.setText(body);
        }

        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = updateET1.getText().toString();
                String name = updateET1.getText().toString();
                String title = updateET1.getText().toString();
                String body = updateET1.getText().toString();

                if(userId != null && name != null && title!= null && body != null){
                    try {
                        updateDataToApi(userId,name,title,body,status);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(UpdateDataActivity.this, "No data to update", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateDataToApi(String userId, String name, String title, String body, String status) throws JSONException {
        String POST_URL = "http://192.168.11.216:80/api/user/update.php";

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
                        Toast.makeText(UpdateDataActivity.this, "Update POST: "+response.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Update POST status: ",response.toString());

                        Intent intent = new Intent(UpdateDataActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UpdateDataActivity.this, "POST Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void init() {
        updateET1 = findViewById(R.id.updateET1);
        updateET2 = findViewById(R.id.updateET2);
        updateET3 = findViewById(R.id.updateET3);
        updateET4 = findViewById(R.id.updateET4);
        updateBTN = findViewById(R.id.updateBTN);
    }
}
