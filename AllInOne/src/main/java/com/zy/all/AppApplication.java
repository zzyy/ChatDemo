package com.zy.all;

import android.app.Application;
import android.content.Context;

/**
 * Created by Simon on 2015/1/13.
 */
public class AppApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
