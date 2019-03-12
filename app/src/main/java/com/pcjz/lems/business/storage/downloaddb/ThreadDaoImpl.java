package com.pcjz.lems.business.storage.downloaddb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pcjz.lems.business.entity.offline.OfflinePeriodInfo;
import com.pcjz.lems.business.entity.offline.ThreadBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kun on 2016/11/10.
 */
public class ThreadDaoImpl implements ThreadDao {

    private DBHelper dbHelper;

    public ThreadDaoImpl(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    @Override
    public synchronized void insertThread(ThreadBean threadBean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into thread_info ( thread_id, url, start ,end, finished, phone) values (?,?,?,?,?,?)"
                , new Object[]{threadBean.getId(), threadBean.getUrl(), threadBean.getStart(), threadBean.getEnd(), threadBean.getFinished(), threadBean.getPhone()});
        db.close();
    }

    @Override
    public synchronized void updateThread(String url, int thread_id, int finished, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update thread_info set finished = ? where url = ? and phone = ? and thread_id = ?"
                , new Object[]{finished, url, phone, thread_id});
        db.close();
    }

    @Override
    public void deleteThread(String url, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from thread_info where url = ? and phone = ?", new Object[]{url, phone});
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
    public List<ThreadBean> getThreads(String url, String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and phone = ?", new String[]{url, phone});
        List<ThreadBean> threadBeanList = new ArrayList<>();
        while (cursor.moveToNext()) {
            ThreadBean bean = new ThreadBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            bean.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            bean.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            bean.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            bean.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            threadBeanList.add(bean);
        }
        cursor.close();
        db.close();
        return threadBeanList;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?", new String[]{url, thread_id + ""});
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }

    @Override
    public void insertPeriod(OfflinePeriodInfo offlinePeriod) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into period_info ( id, url, length, finished, phone) values (?,?,?,?,?)"
                , new Object[]{offlinePeriod.getId(), offlinePeriod.getUrl() ,offlinePeriod.getLength(), offlinePeriod.getFinished(), offlinePeriod.getPhone()});
        db.close();
    }

    @Override
    public void updatePeriod(String url, String id, int finished, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update period_info set finished = ? where url = ? and id = ? and phone = ?"
                , new Object[]{finished, url, id, phone});
        db.close();
    }

    @Override
    public void deletePeriod(String id, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from period_info where id = ? and phone = ?", new Object[]{id, phone});
        db.close();
    }

    @Override
    public List<OfflinePeriodInfo> getPeriods(String url, String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from period_info where url = ? and phone = ?", new String[]{url, phone});
        List<OfflinePeriodInfo> periods = new ArrayList<>();
        while (cursor.moveToNext()) {
            OfflinePeriodInfo bean = new OfflinePeriodInfo();
            bean.setId(cursor.getString(cursor.getColumnIndex("id")));
            bean.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            bean.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            periods.add(bean);
        }
        cursor.close();
        db.close();
        return periods;
    }
}
