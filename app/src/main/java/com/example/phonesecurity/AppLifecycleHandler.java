package com.example.phonesecurity;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    LifeCycleDelegate lifeCycleDelegate;
    boolean appInForeground = false;

    public AppLifecycleHandler(LifeCycleDelegate lifeCycleDelegate) {
        this.lifeCycleDelegate = lifeCycleDelegate;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (!appInForeground) {
            appInForeground = true;
            lifeCycleDelegate.onAppForegrounded();
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // lifecycleDelegate instance was passed in on the constructor
            appInForeground = false;
            lifeCycleDelegate.onAppBackgrounded();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {

    }

    @Override
    public void onLowMemory() {

    }
}
