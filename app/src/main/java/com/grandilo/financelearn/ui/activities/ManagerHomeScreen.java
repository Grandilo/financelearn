package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.StaffAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class ManagerHomeScreen extends AppCompatActivity {

    private RecyclerView staffRecyclerView;
    private JSONObject signedInUserObject;

    private List<HashMap<String, Object>> staffList = new ArrayList<>();

    private StaffAdapter staffAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_home_screen);
        signedInUserObject = AppPreferences.getSignedInUser(this);
        staffRecyclerView = (RecyclerView) findViewById(R.id.staff_recycler_view);
        initStaffAdapter();
        fetchAllStaffToAssignCourses();
    }


    private void initStaffAdapter() {
        staffAdapter = new StaffAdapter(this, staffList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        staffRecyclerView.setLayoutManager(linearLayoutManager);
        staffRecyclerView.setAdapter(staffAdapter);
    }

    private void fetchAllStaffToAssignCourses() {
        if (signedInUserObject != null) {

            FirebaseUtils.getNotificationsNode().child(signedInUserObject.optString("staff_id")).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    GenericTypeIndicator<HashMap<String, Object>> notifGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                    };
                    HashMap<String, Object> staffProps = dataSnapshot.getValue(notifGenericTypeIndicator);
                    if (staffProps != null) {
                        if (!staffList.contains(staffProps)) {
                            staffList.add(staffProps);
                            staffAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<HashMap<String, Object>> notifGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                    };
                    HashMap<String, Object> staffProps = dataSnapshot.getValue(notifGenericTypeIndicator);
                    if (staffProps != null) {
                        if (staffList.contains(staffProps)) {
                            staffList.remove(staffProps);
                            staffAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

    }

}
