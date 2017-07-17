package com.grandilo.financelearn.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.StaffCourseListAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.UiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings("deprecation")
@SuppressLint("StaticFieldLeak")
public class StaffProfileManagementScreen extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView staffCoursesRecyclerView;
    private String staffName, staffId;

    private String sourceActivity;
    private TextView headerMessage;

    private static LinearLayout bottomBar;
    private DatabaseReference courseReference;
    private static StaffCourseListAdapter staffCourseListAdapter;

    private List<HashMap<String, String>> courses = new ArrayList<>();
    private ChildEventListener coursesEventListener;
    private JSONObject signedInUserObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_profile_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        signedInUserObject = AppPreferences.getSignedInUser(this);

        staffCoursesRecyclerView = (RecyclerView) findViewById(R.id.staff_course_recycler_view);
        TextView assignCoursesButton = (TextView) findViewById(R.id.assign_courses_button);
        assignCoursesButton.setOnClickListener(this);

        headerMessage = (TextView) findViewById(R.id.header_message);
        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        offloadIntent();
        courseReference = FirebaseUtils.getCourses();
        initCoursesAdapter();
        fetchCourses();
    }

    public static void reviewSelection(){
        int selectedCoursesCount = staffCourseListAdapter.getCoursesToTest().size();
        if (selectedCoursesCount == 2) {
            bottomBar.setVisibility(View.VISIBLE);
        } else {
            bottomBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (courseReference != null && coursesEventListener != null) {
            courseReference.removeEventListener(coursesEventListener);
        }
    }

    private void initCoursesAdapter() {
        staffCourseListAdapter = new StaffCourseListAdapter(this, courses);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        staffCoursesRecyclerView.setLayoutManager(verticalLayoutManager);
        staffCoursesRecyclerView.setAdapter(staffCourseListAdapter);
    }

    private void fetchCourses() {

        coursesEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<HashMap<String, String>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {
                };

                HashMap<String, String> courseProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                if (courseProps != null) {
                    courseProps.put(FinanceLearningConstants.COURSE_ID, dataSnapshot.getKey());
                }

                courses.add(courseProps);
                staffCourseListAdapter.notifyDataSetChanged();
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

        };
        courseReference.addChildEventListener(coursesEventListener);
    }

    private void offloadIntent() {
        Bundle intentExtras = getIntent().getExtras();
        staffName = intentExtras.getString(FinanceLearningConstants.EMPLOYEE_NAME);
        headerMessage.setText(Html.fromHtml("Assign <b>" + staffName + "</b> two test Courses"));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(staffName);
        }
        staffId = intentExtras.getString(FinanceLearningConstants.EMPLOYEE_ID);
        sourceActivity = intentExtras.getString(FinanceLearningConstants.SOURCE_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        if (sourceActivity == null) {
            Intent staffListManagementIntent = new Intent(this, ManagerHomeScreen.class);
            startActivity(staffListManagementIntent);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.assign_courses_button){
            UiUtils.showProgressDialog(this,"Assigning Courses");
            HashMap<String, Object> updatableProps = new HashMap<>();
            updatableProps.put(FinanceLearningConstants.COURSES_ASSIGNED, staffCourseListAdapter.getCoursesToTest());

            FirebaseUtils.getStaffReference().child(staffId).updateChildren(updatableProps, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {

                        HashMap<String,Object>coursesAssignedNotifProps = new HashMap<>();
                        coursesAssignedNotifProps.put(FinanceLearningConstants.NOTIFICATIONS_TARGET,staffId);
                        coursesAssignedNotifProps.put(FinanceLearningConstants.NOTIFICATION_CATEGORY,FinanceLearningConstants.CATEGORY_COURSES_ASSIGNED_TO_ME_JUST_NOW);

                        FirebaseUtils.getNotificationsNode().push().setValue(coursesAssignedNotifProps, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError==null){
                                    Toast.makeText(StaffProfileManagementScreen.this, "The two courses were successfully assigned to " + staffName, Toast.LENGTH_LONG).show();

                                    //Delete the notification path if any
                                    FirebaseUtils.getNotificationsNode().orderByChild(FinanceLearningConstants.NOTIFICATIONS_TARGET).
                                            equalTo(signedInUserObject.optString("staff_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UiUtils.dismissProgressDialog();
                                            if (dataSnapshot.getChildrenCount() > 0) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    GenericTypeIndicator<HashMap<String, Object>> notifGenericTypeIndicator =
                                                            new GenericTypeIndicator<HashMap<String, Object>>() {
                                                            };
                                                    HashMap<String, Object> notifProps = snapshot.getValue(notifGenericTypeIndicator);
                                                    if (notifProps != null) {
                                                        String employeeId = (String) notifProps.get(FinanceLearningConstants.EMPLOYEE_ID);
                                                        if (employeeId.equals(staffId)) {
                                                            FirebaseUtils.getNotificationsNode().child(snapshot.getKey()).removeValue();
                                                        }
                                                    }
                                                }
                                            } else {
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else{
                                    Toast.makeText(StaffProfileManagementScreen.this, "Sorry, an error occurred while assigning the selected courses. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(StaffProfileManagementScreen.this, "Sorry, an error occurred while assigning the selected courses. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
