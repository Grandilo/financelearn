package com.grandilo.financelearn.ui;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.grandilo.financelearn.PushReceptionService;

/**
 * @author Ugo
 */

public class ApplicationLoader extends Application {

    public static int NOTIFICATION_COUNT = 0;
    private static ApplicationLoader _INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
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

}
