package com.example.restapiretrofitdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "User.db";
    public static String TABLE_NAME = "User";
    public static String TABLE2_NAME = "allUsers";
    public static int DB_VERSION = 1;

    public static String COL_userId = "userId";
    public static String COL_name = "name";
    public static String COL_title = "title";
    public static String COL_body = "body";
    public static String COL_status = "status";

    public static String TABLE2_COL_userId = "userId";
    public static String TABLE2_COL_name = "name";
    public static String TABLE2_COL_title = "title";
    public static String TABLE2_COL_body = "body";
    public static String TABLE2_COL_status = "status";

    //Sql Queries
    public String create_table = "create table "+TABLE_NAME+"(userId String,name String,title String,body String,status String)";
    public String create_table2 = "create table "+TABLE2_NAME+"(userId String,name String,title String,body String,status String)";
    public String show_all_data_table1 = "select * from "+TABLE_NAME+"";
    public String show_all_data_table2 = "select * from "+TABLE2_NAME+"";
    public String show_unSynced_data = "select * from User where status= 'Not Synced'";
    public String show_Synced_data = "select * from User where status= 'Synced'";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table);
        db.execSQL(create_table2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor showDataFromLocalDB(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(show_all_data_table1,null);

        return cursor;
    }

    public Cursor showALLDataTable2(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(show_all_data_table2,null);

        return cursor;
    }

    public void insertDataIntoLocalDB(String userId,String name,String title,String body,String status) {
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(COL_userId,userId);
        contentValues1.put(COL_name,name);
        contentValues1.put(COL_title,title);
        contentValues1.put(COL_body,body);
        contentValues1.put(COL_status,status);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues1);
        sqLiteDatabase.close();

    }

    public void insertDataIntoTable2(String userId,String name,String title,String body,String status){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE2_COL_userId,userId);
        contentValues.put(TABLE2_COL_name,name);
        contentValues.put(TABLE2_COL_title,title);
        contentValues.put(TABLE2_COL_body,body);
        contentValues.put(TABLE2_COL_status,status);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE2_NAME,null,contentValues);
        sqLiteDatabase.close();

    }

    public Cursor showUnSyncedData(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor1 = sqLiteDatabase.rawQuery(show_unSynced_data,null);

        return cursor1;
    }

    public void deleteSingleData(String id){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,"userId=?",new String[]{String.valueOf(id)});
    }

}