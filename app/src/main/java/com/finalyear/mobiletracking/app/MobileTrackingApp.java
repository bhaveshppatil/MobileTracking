package com.finalyear.mobiletracking.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.finalyear.mobiletracking.sharePref.SharedPrefs;

public class MobileTrackingApp extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        MultiDex.install(this);
        SharedPrefs.initialize(this);
         //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }
}
