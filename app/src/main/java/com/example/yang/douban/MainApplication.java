package com.example.yang.douban;
import java.util.HashMap;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by ouyangshen on 2016/10/1.
 */

public class MainApplication extends Application {
    private static Context context;
    private final static String TAG = "MainApplication";
    private static MainApplication mApp;
    public HashMap<String, Integer> mInfoMap = new HashMap<String, Integer>();

    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        context = getApplicationContext();
        Log.d(TAG, "onCreate");
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }

    public HashMap<Long, Bitmap> mIconMap = new HashMap<Long, Bitmap>();

}
