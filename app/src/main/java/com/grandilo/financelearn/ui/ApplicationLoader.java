package com.grandilo.financelearn.ui;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by wan on 7/10/17.
 */

public class ApplicationLoader extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
