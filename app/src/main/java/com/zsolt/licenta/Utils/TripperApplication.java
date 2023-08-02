package com.zsolt.licenta.Utils;

import android.app.Application;
import android.content.SharedPreferences;
public class TripperApplication extends Application {

    private TripperActivityLifecycleCallbacks tripperActivityLifecycleCallbacks;
    private SharedPreferences sharedPreferences;
    private String title, message;

    @Override
    public void onCreate() {
        super.onCreate();
    }


}
