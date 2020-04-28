package com.cjlu.tyweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    public static SQLiteDatabase database;

    /**
     * 初始化数据库
     */
    public static void initDB(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * 查找数据库中的城市列表
     */
    public static List<String> queryAllCityName() {
        Cursor cursor = database.query("info", null, null, null, null, null, null);
        List<String> cityList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex("city"));
            cityList.add(city);
        }
        return cityList;
    }

    /**
     * 根据城市名称去更新数据库数据
     */
    public static int updateInfoByCity(String city, String content) {
        ContentValues values = new ContentValues();
        values.put("content", content);
        return database.update("info", values, "city=?", new String[]{city});
    }

    /**
     * 新增城市记录
     */
    public static long addCityInfo(String city, String content) {
        ContentValues values = new ContentValues();
        values.put("city", city);
        values.put("content", content);
        return database.insert("info", null, values);
    }

    /**
     * 根据城市，查询数据库中数据
     */
    public static String queryInfoByCity(String city) {
        Cursor cursor = database.query("info", null, "city=?", new String[]{city}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("content"));
            return content;
        }
        return null;
    }

    /**
     * 存储城市天气上限
     */
    public static int getCityCount() {
        Cursor cursor = database.query("info", null, null, null, null, null, null);
        int count = cursor.getCount();
        return count;
    }

    /**
     * 查询数据库中全部信息
     */
    public static List<DatabaseBean> queryAllInfo() {
        Cursor cursor = database.query("info", null, null, null, null, null, null);
        List<DatabaseBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            DatabaseBean bean = new DatabaseBean(id, city, content);
            list.add(bean);
        }
        return list;
    }

    /**
     * 删除城市记录
     */
    public static int deleteCityInfo(String city) {
        return database.delete("info", "city=?", new String[]{city});
    }

    /**
     * 清除所有缓存
     */
    public static void deleteAll() {
        String sql = "delete from info";
        database.execSQL(sql);
    }
}
