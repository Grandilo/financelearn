package com.grandilo.financelearn.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.grandilo.financelearn.PushReceptionService;

/**
 * @author Ugo
 */

public class ApplicationLoader extends Application implements Application.ActivityLifecycleCallbacks {

    public static int NOTIFICATION_COUNT = 0;
    private static ApplicationLoader _INSTANCE;

    private int numStarted;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        _INSTANCE = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.INFO);
        startNotificationService();
    }

    public static ApplicationLoader getInstance() {
        return _INSTANCE;
    }

    private void startNotificationService() {
        Intent notificationServiceIntent = new Intent(this, PushReceptionService.class);
        startService(notificationServiceIntent);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        numStarted++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        numStarted--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
