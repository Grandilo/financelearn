package com.grandilo.financelearn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.notifs.NotifUtils;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Ugo
 */

public class PushReceptionService extends Service {

    private JSONObject signedInUserObject;

    @Override
    public void onCreate() {
        super.onCreate();
        signedInUserObject = AppPreferences.getSignedInUser(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (signedInUserObject != null) {

            FirebaseUtils.getNotificationsNode()
                    .orderByChild(FinanceLearningConstants.NOTIFICATIONS_TARGET)
                    .equalTo(signedInUserObject.optString("staff_id"))
                    .addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            GenericTypeIndicator<HashMap<String, Object>> notifGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};

                            HashMap<String, Object> notifProps = dataSnapshot.getValue(notifGenericTypeIndicator);

                            if (notifProps != null) {
                                NotifUtils.blowNotification(notifProps);
                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


        }

        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
