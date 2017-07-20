package com.grandilo.financelearn.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.UploadCourseVideoActivity;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class EmployeeHomeScreen extends AppCompatActivity implements View.OnClickListener {

    private JSONObject signedInUserProps;

    private ChildEventListener coursesEventListener;
    private DatabaseReference courseReference;

    JSONArray allPretestCourses;
    List<String> pretestCourseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);
        initViews();
        populateSignedInUserProps();

        allPretestCourses = signedInUserProps.optJSONArray(FinanceLearningConstants.ALL_PRETEST_COURSES);

        if (allPretestCourses != null) {
            Log.d("ResultTag","AllPretest Courses are not null");
            for (int i = 0; i < allPretestCourses.length(); i++) {
                String courseId = allPretestCourses.optString(i);
                if (courseId != null) {
                    if (!pretestCourseList.contains(courseId)) {
                        pretestCourseList.add(courseId);
                    }
                }
            }
        }else {
            Log.d("ResultTag","AllPretest Courses are null");
        }

        courseReference = FirebaseUtils.getCourses();
        fetchCourses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        signedInUserProps = AppPreferences.getSignedInUser(this);
        if (signedInUserProps == null) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (courseReference != null && coursesEventListener != null) {
            courseReference.removeEventListener(coursesEventListener);
        }
    }

    private void finishLogOut() {
        AppPreferences.saveLoggedInType(EmployeeHomeScreen.this, null);
        Intent splashScreenIntent = new Intent(EmployeeHomeScreen.this, SplashActivity.class);
        startActivity(splashScreenIntent);
    }

    private void populateSignedInUserProps() {
        signedInUserProps = AppPreferences.getSignedInUser(this);
        if (signedInUserProps != null) {
            String lastName = signedInUserProps.optString(FinanceLearningConstants.LAST_NAME);
            String surname = signedInUserProps.optString(FinanceLearningConstants.SURNAME);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Html.fromHtml("Welcome, <b>" + surname + " " + lastName + "</b>"));
            }
        }
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        View myCoursesView = findViewById(R.id.my_courses_view);
        myCoursesView.setOnClickListener(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (signedInUserProps != null) {
            MenuItem uploadVideoItem = menu.findItem(R.id.upload_video);
            try {
                if (signedInUserProps.get("staff_id").equals("wan")
                        || signedInUserProps.get("staff_id").equals("cali")
                        || signedInUserProps.get("staff_id").equals("ugo")) {
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
        getMenuInflater().inflate(R.menu.menu_employee, menu);
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
            AppPreferences.saveLoggedInUser(EmployeeHomeScreen.this, null);
            finishLogOut();
        } else if (item.getItemId() == R.id.upload_video) {
            Intent uploadVideoIntent = new Intent(this, UploadCourseVideoActivity.class);
            startActivity(uploadVideoIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_courses_view:
                boolean mainTestTaken = signedInUserProps.optBoolean(FinanceLearningConstants.MAIN_TEST_TAKEN, false);
                if (!mainTestTaken) {
                    boolean pretestTaken = signedInUserProps.optBoolean(FinanceLearningConstants.PRETEST_TAKEN, false);
                    if (pretestTaken) {
                        Intent pretestResultIntent = new Intent(EmployeeHomeScreen.this, PreTestResultActivity.class);
                        pretestResultIntent.putExtra(FinanceLearningConstants.TOTAL_NO_OF_QS, pretestCourseList.size());
                        startActivity(pretestResultIntent);
                    } else {
                        Intent preTestIntent = new Intent(EmployeeHomeScreen.this, PreTestCourseSelectionActivity.class);
                        startActivity(preTestIntent);
                    }
                }
                break;
        }
    }

    private void initPasswordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Your Password");
        builder.setMessage("Do you wish to update your password?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mUpdatePasswordIntent = new Intent(EmployeeHomeScreen.this, UpdatePasswordActivity.class);
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

    private void fetchCourses() {

        coursesEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<HashMap<String, String>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {
                };

                HashMap<String, String> courseProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                String courseKey = dataSnapshot.getKey();

                if (pretestCourseList.contains(courseKey)) {
                    if (courseProps != null) {
                        String courseName = courseProps.get(FinanceLearningConstants.COURSE_NAME);
                        FinanceLearningConstants.pretestCourseMap.put(dataSnapshot.getKey(), courseName);
                    }
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

        };
        courseReference.addChildEventListener(coursesEventListener);
    }

}
