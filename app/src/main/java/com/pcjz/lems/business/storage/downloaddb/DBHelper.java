package com.pcjz.lems.business.storage.downloaddb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kun on 2016/11/10.
 */
public class DBHelper extends SQLiteOpenHelper{

    //创建数据库信息
    public static String DB_NAME = "lemsdownload.db";
    private static DBHelper dbHelper = null;

    public static DBHelper getInstance(Context context){
        if(dbHelper==null) dbHelper = new DBHelper(context);
        return dbHelper;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlThread = "create table thread_info (_id integer primary key autoincrement," +
                "thread_id integer,url text,start integer,end integer,finished integer,phone text)";
        //创建表存储当前item（项目期信息）
        String sqlPeriod = "create table period_info (_id integer primary key autoincrement," +
                "id text,url text,length integer,finished integer,phone text)";
        //创建表存储当前item（app应用信息）
        String sqlApp = "create table app_info (_id integer primary key autoincrement," +
                "id text,url text,length integer,finished integer,versionCode text,basicUrl text,appPackageName text,fileSize float,appIcon text,appName text)";
        db.execSQL(sqlThread);
        db.execSQL(sqlPeriod);
        db.execSQL(sqlApp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
