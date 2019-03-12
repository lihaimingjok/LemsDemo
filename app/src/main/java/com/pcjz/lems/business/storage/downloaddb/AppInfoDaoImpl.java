package com.pcjz.lems.business.storage.downloaddb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/24 15:40
 */
public class AppInfoDaoImpl implements AppInfoDao {

    private DBHelper dbHelper;

    public AppInfoDaoImpl(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    @Override
    public void insertApp(AppInfoBean appBean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into app_info ( id, url, length, finished, versionCode, basicUrl, appPackageName, fileSize, appIcon, appName) values (?,?,?,?,?,?,?,?,?,?)"
                , new Object[]{appBean.getId(), appBean.getUrl(), appBean.getLength(), appBean.getFinished(), appBean.getVersionCode(), appBean.getBasicUrl(),
                appBean.getAppPackageName(), appBean.getFileSize(), appBean.getAppIcon(), appBean.getAppName()});
        db.close();
    }

    @Override
    public void updateApp(String basicUrl, String id, int finished) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update app_info set finished = ? where basicUrl = ? and id = ?"
                , new Object[]{finished, basicUrl,  id});
        db.close();
    }

    @Override
    public void updateVersionApp(String basicUrl, String id, String versionCode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update app_info set versionCode = ? where basicUrl = ? and id = ?"
                , new Object[]{versionCode, basicUrl,  id});
        db.close();
    }

    @Override
    public void deleteApp(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from app_info where id = ?", new Object[]{id});
        db.close();
    }

    /*public boolean delete(String table) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(table,null,null) > 0;
    }*/

    public void clearTable(String table){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + table +";";
        db.execSQL(sql);
    }

    @Override
    public List<AppInfoBean> getApps(String basicUrl) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from app_info where basicUrl = ?", new String[]{basicUrl});
        List<AppInfoBean> apps = new ArrayList<>();
        while (cursor.moveToNext()) {
            AppInfoBean bean = new AppInfoBean();
            bean.setId(cursor.getString(cursor.getColumnIndex("id")));
            bean.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            bean.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            bean.setVersionCode(cursor.getString(cursor.getColumnIndex("versionCode")));
            bean.setAppPackageName(cursor.getString(cursor.getColumnIndex("appPackageName")));
            bean.setAppIcon(cursor.getString(cursor.getColumnIndex("appIcon")));
            bean.setAppName(cursor.getString(cursor.getColumnIndex("appName")));
            bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            bean.setBasicUrl(cursor.getString(cursor.getColumnIndex("basicUrl")));
            bean.setFileSize(cursor.getFloat(cursor.getColumnIndex("fileSize")));
            apps.add(bean);
        }
        cursor.close();
        db.close();
        return apps;
    }

    @Override
    public boolean isExists(String basicUrl) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from app_info where basicUrl = ?", new String[]{basicUrl});
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }
}
