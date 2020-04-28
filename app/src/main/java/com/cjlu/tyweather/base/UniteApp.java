package com.cjlu.tyweather.base;

import android.app.Application;

import com.cjlu.tyweather.db.DbManager;

import org.xutils.x;

public class UniteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        DbManager.initDB(this);
    }
}
