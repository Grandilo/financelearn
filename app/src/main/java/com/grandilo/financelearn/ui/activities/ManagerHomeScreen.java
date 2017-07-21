package com.grandilo.financelearn.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.UploadCourseVideoActivity;
import com.grandilo.financelearn.ui.adapters.StaffListAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class ManagerHomeScreen extends AppCompatActivity {

    private RecyclerView staffRecyclerView;
    private JSONObject signedInUserProps;

    private List<HashMap<String, Object>> staffList = new ArrayList<>();
    private StaffListAdapter staffListAdapter;

    private TextView staffToAssignContentView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_home_screen);
        toolbar = (Toolbar)findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        signedInUserProps = AppPreferences.getSignedInUser(this);
        staffRecyclerView = (RecyclerView) findViewById(R.id.staff_recycler_view);
        staffToAssignContentView = (TextView) findViewById(R.id.content_empty_view);
        initStaffAdapter();
        fetchAllStaffToAssignCourses();
    }

    private void initStaffAdapter() {
        staffListAdapter = new StaffListAdapter(this, staffList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        staffRecyclerView.setLayoutManager(linearLayoutManager);
        staffRecyclerView.setAdapter(staffListAdapter);
    }

    private void fetchAllStaffToAssignCourses() {

        if (signedInUserProps != null) {

            FirebaseUtils.getNotificationsNode().orderByChild(FinanceLearningConstants.NOTIFICATIONS_TARGET)
                    .equalTo(signedInUserProps.optString("staff_id"))
                    .addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            GenericTypeIndicator<HashMap<String, Object>> notifGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                            };
                            HashMap<String, Object> staffProps = dataSnapshot.getValue(notifGenericTypeIndicator);
                            if (staffProps != null) {
                                if (!staffList.contains(staffProps)) {
                                    staffList.add(staffProps);
                                    staffListAdapter.notifyDataSetChanged();
                                }
                            }
                            staffToAssignContentView.setVisibility(View.GONE);
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
                                    staffListAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (signedInUserProps!=null){
            MenuItem uploadVideoItem = menu.findItem(R.id.upload_video);
            try {
                if (signedInUserProps.get("staff_id").equals("wan")
                        || signedInUserProps.get("staff_id").equals("cali")
                        || signedInUserProps.get("staff_id").equals("ugo")){
                    uploadVideoItem.setVisible(true);
                    supportInvalidateOptionsMenu();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.update_password) {
            initPasswordResetDialog();
            return true;
        } else if (item.getItemId() == R.id.sign_out) {
            AppPreferences.saveLoggedInUser(ManagerHomeScreen.this, null);
            finishLogOut();
        }else if (item.getItemId()==R.id.upload_video){
            Intent uploadVideoIntent = new Intent(this, UploadCourseVideoActivity.class);
            startActivity(uploadVideoIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishLogOut() {
        AppPreferences.saveLoggedInType(ManagerHomeScreen.this, null);
        Intent splashScreenIntent = new Intent(ManagerHomeScreen.this, SplashActivity.class);
        startActivity(splashScreenIntent);
    }
    private void initPasswordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Your Password");
        builder.setMessage("Do you wish to update your password?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mUpdatePasswordIntent = new Intent(ManagerHomeScreen.this, UpdatePasswordActivity.class);
                startActivity(mUpdatePasswordIntent);
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
