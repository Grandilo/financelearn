package com.grandilo.financelearn.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.UploadCourseVideoActivity;
import com.grandilo.financelearn.ui.adapters.StaffListAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
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
    private JSONObject signedInUserProps;

    private List<HashMap<String, Object>> staffList = new ArrayList<>();
    private StaffListAdapter staffListAdapter;

    private TextView staffToAssignContentView;

    private ProgressDialog progressDialog;

    private List<String> staffListIds = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        signedInUserProps = AppPreferences.getSignedInUser(this);
        staffRecyclerView = (RecyclerView) findViewById(R.id.staff_recycler_view);
        staffToAssignContentView = (TextView) findViewById(R.id.content_empty_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading staff list...");
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
        } else if (item.getItemId() == R.id.upload_video) {
            Intent uploadVideoIntent = new Intent(this, UploadCourseVideoActivity.class);
            startActivity(uploadVideoIntent);
        } else if (item.getItemId() == R.id.reset_tests_for_a_staff) {
            progressDialog.show();
            loadAllStaff();
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishLogOut() {
        AppPreferences.saveLoggedInType(ManagerHomeScreen.this, null);
        Intent splashScreenIntent = new Intent(ManagerHomeScreen.this, SplashActivity.class);
        startActivity(splashScreenIntent);
        finish();
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

    public void loadAllStaff() {
        FirebaseUtils.getStaffReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot != null) {
                        GenericTypeIndicator<HashMap<String, Object>> notifGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                        HashMap<String, Object> staffProps = snapshot.getValue(notifGenericTypeIndicator);
                        if (staffProps != null) {
                            String staffId = (String) staffProps.get("staff_id");
                            if (staffId != null) {
                                String category = (String) staffProps.get("category");
                                if (category.equals("employee")) {
                                    if (!staffListIds.contains(staffId)) {
                                        staffListIds.add(staffId);
                                    }
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initStaffListDialog();
            }
        }, 1000);

    }


    private void initStaffListDialog() {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select staff to reset test for");
        builder.setItems(staffListIds.toArray(new CharSequence[staffListIds.size()]), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                HashMap<String, Object> updatableProps = new HashMap<>();

                updatableProps.put(FinanceLearningConstants.PRETEST_TAKEN, null);
                updatableProps.put(FinanceLearningConstants.PRETEST_RIGHT_ANSWERS, null);
                updatableProps.put(FinanceLearningConstants.PRETEST_WRONG_ANSWERS, null);

                updatableProps.put(FinanceLearningConstants.MAIN_TEST_TAKEN, null);
                updatableProps.put(FinanceLearningConstants.MAIN_TEST_WRONG_ANSWERS, null);
                updatableProps.put(FinanceLearningConstants.MAIN_TEST_RIGHT_ANSWERS, null);

                updatableProps.put(FinanceLearningConstants.TOTAL_NO_OF_QS, null);
                updatableProps.put(FinanceLearningConstants.ALL_PRETEST_COURSES, null);

                progressDialog.setMessage("Resetting tests, please wait...");
                progressDialog.show();

                FirebaseUtils.getStaffReference().child(staffListIds.get(i)).updateChildren(updatableProps, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        progressDialog.dismiss();

                        if (databaseError == null) {
                            Toast.makeText(ManagerHomeScreen.this, "Tests reset successfully. The staff may retake the tests now", Toast.LENGTH_LONG).show();
                            try {
                                FinanceLearningConstants.pretestRightAnswers.clear();
                                FinanceLearningConstants.pretestWrongAnswers.clear();
                                FinanceLearningConstants.coursesToTest.clear();
                                FinanceLearningConstants.courseIdNameMap.clear();
                                FinanceLearningConstants.pickedOptions.clear();
                            } catch (Exception ignored) {

                            }
                        } else {
                            Toast.makeText(ManagerHomeScreen.this, "Error during test reset. Please try again.", Toast.LENGTH_LONG).show();
                        }

                    }

                });

            }

        });

        builder.create();
        builder.show();

    }

}
