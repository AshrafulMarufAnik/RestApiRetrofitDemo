package com.example.restapiretrofitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    private RecyclerView dataRV;
    private ArrayList<Data> dataArrayList;
    private DataAdapter dataAdapter;
    private DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        init();
        getUnSyncedData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUnSyncedData();
            }
        });
    }

    private void getUnSyncedData() {
        dataArrayList.clear();
        Cursor cursor = databaseHelper.showDataFromLocalDB();

        if(cursor.getExtras()!=null){
            while (cursor.moveToNext()){
                String userId = cursor.getString(cursor.getColumnIndex(String.valueOf(databaseHelper.COL_userId)));
                String name = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_name));
                String title = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_title));
                String body = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_body));
                String status = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_status));

                Data singleDataObj = new Data(userId,name,title,body,status);
                dataArrayList.add(singleDataObj);
            }
            dataAdapter = new DataAdapter(dataArrayList,this);
            dataRV.setAdapter(dataAdapter);
            dataAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void init() {
        dataRV = findViewById(R.id.dataRV);
        swipeRefreshLayout = findViewById(R.id.swipeRef2);
        databaseHelper = new DatabaseHelper(this);
        dataArrayList = new ArrayList<>();
        dataRV.setLayoutManager(new LinearLayoutManager(this));

    }
}
