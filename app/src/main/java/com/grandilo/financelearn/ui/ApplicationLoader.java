package com.grandilo.financelearn.ui;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.PushReceptionService;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.jenzz.appstate.AppStateListener;
import com.jenzz.appstate.AppStateMonitor;

import org.json.JSONObject;

import java.util.HashMap;

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
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.INFO);

        final JSONObject signedInUserProps = AppPreferences.getSignedInUser(ApplicationLoader.this);

        AppStateMonitor appStateMonitor = AppStateMonitor.create(this);
//        appStateMonitor.addListener(new AppStateListener() {
//
//            @Override
//            public void onAppDidEnterForeground() {
//
//                if (signedInUserProps != null) {
//                    boolean loggedIn = signedInUserProps.optBoolean(FinanceLearningConstants.LOGGED_IN_STATUS, false);
//                    if (loggedIn && !signedInUserProps.optString("staff_id").equals("guest")) {
//                        Toast.makeText(ApplicationLoader.this, "Another instance of this account is already logged in.", Toast.LENGTH_LONG).show();
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                    } else {
//                        String staffId = signedInUserProps.optString("staff_id");
//                        HashMap<String, Object> updatableProps = new HashMap<>();
//                        updatableProps.put(FinanceLearningConstants.LOGGED_IN_STATUS, true);
//                        FirebaseUtils.getStaffReference().child(staffId).updateChildren(updatableProps, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if (databaseError == null) {
//                                    FirebaseUtils.getStaffReference().child(signedInUserProps.optString("staff_id")).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot != null) {
//                                                GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
//                                                };
//                                                HashMap<String, Object> newUserProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
//                                                if (newUserProps != null) {
//                                                    AppPreferences.saveLoggedInUser(ApplicationLoader.this, newUserProps);
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//
//                                    });
//
//                                }
//                            }
//                        });
//
//                    }
//                }
//
//            }
//
//            @Override
//            public void onAppDidEnterBackground() {
//                if (signedInUserProps != null) {
//
//                    boolean loggedIn = signedInUserProps.optBoolean(FinanceLearningConstants.LOGGED_IN_STATUS, false);
//                    if (loggedIn) {
//                        String staffId = signedInUserProps.optString("staff_id");
//                        HashMap<String, Object> updatableProps = new HashMap<>();
//                        updatableProps.put(FinanceLearningConstants.LOGGED_IN_STATUS, false);
//                        FirebaseUtils.getStaffReference().child(staffId).updateChildren(updatableProps, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if (databaseError == null) {
//                                    FirebaseUtils.getStaffReference().child(signedInUserProps.optString("staff_id")).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot != null) {
//                                                GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
//                                                };
//                                                HashMap<String, Object> newUserProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
//                                                if (newUserProps != null) {
//                                                    AppPreferences.saveLoggedInUser(ApplicationLoader.this, newUserProps);
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//
//                                    });
//
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//
//        });

        appStateMonitor.start();
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
