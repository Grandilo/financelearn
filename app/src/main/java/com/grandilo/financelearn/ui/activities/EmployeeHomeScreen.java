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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.UploadCourseVideoActivity;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.GsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("deprecation")
public class EmployeeHomeScreen extends AppCompatActivity implements View.OnClickListener {

    private JSONObject signedInUserProps;

    private ChildEventListener coursesEventListener;
    private DatabaseReference courseReference;

    private List<String> listOfSelectedCourseIds = new ArrayList<>();
    private View myCoursesView;
    private TextView librariesOrVideoDemosTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);

        initViews();
        populateSignedInUserProps();
        String signedInUserId = signedInUserProps.optString("staff_id");

        if (signedInUserId.equals("guest")) {
            myCoursesView.setVisibility(View.GONE);
            librariesOrVideoDemosTextView.setText(getString(R.string.demo_videos));
        } else {
            librariesOrVideoDemosTextView.setText(getString(R.string.libraries));
        }

        String previouslySelectedCourseIds = signedInUserProps.optString(FinanceLearningConstants.ALL_SELECTED_COURSE_IDS);
        String previousPretestResult = signedInUserProps.optString(FinanceLearningConstants.PRETEST_RESULT);
        String previousMainTestResult = signedInUserProps.optString(FinanceLearningConstants.MAIN_TEST_RESULT);

        prepareUserSelectedCourseIds(previouslySelectedCourseIds);

        checkDumpUserSelectedCourseIds();

        offloadPreviousPretestResultIfAvailable(previousPretestResult);
        offloadPreviousMainTestResultIfAvailable(previousMainTestResult);

        courseReference = FirebaseUtils.getCourses();
        fetchCourses();
        fetchUpdatedUserInfo();

    }

    private void prepareUserSelectedCourseIds(String previouslySelectedCourseIds) {
        if (previouslySelectedCourseIds != null) {
            try {
                JSONArray previouslySelectedPretestCoursesJSONArray = new JSONArray(previouslySelectedCourseIds);
                for (int i = 0; i < previouslySelectedPretestCoursesJSONArray.length(); i++) {
                    String courseId = previouslySelectedPretestCoursesJSONArray.optString(i);
                    if (courseId != null) {
                        if (!listOfSelectedCourseIds.contains(courseId)) {
                            listOfSelectedCourseIds.add(courseId);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDumpUserSelectedCourseIds() {
        if (!listOfSelectedCourseIds.isEmpty()) {
            FinanceLearningConstants.idsOfCoursesToTest.clear();
            for (String courseId : listOfSelectedCourseIds) {
                FinanceLearningConstants.idsOfCoursesToTest.add(courseId);
                FinanceLearningConstants.checkedPositions.put(courseId.hashCode(), true);
            }
        }
    }

    private void offloadPreviousMainTestResultIfAvailable(String previousMainTestResult) {
        if (previousMainTestResult != null) {
            try {
                FinanceLearningConstants.mainTestResult.clear();
                JSONObject mainTestJSONObject = new JSONObject(previousMainTestResult);
                Iterator<String> keysItr = mainTestJSONObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Object value = mainTestJSONObject.get(key);
                    FinanceLearningConstants.mainTestResult.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void offloadPreviousPretestResultIfAvailable(String previousPretestResult) {
        if (previousPretestResult != null) {
            try {
                FinanceLearningConstants.pretestResult.clear();
                JSONObject pretestJSONObject = new JSONObject(previousPretestResult);
                Iterator<String> keysItr = pretestJSONObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Object value = pretestJSONObject.get(key);
                    FinanceLearningConstants.pretestResult.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        clearPreviousAnswers();
        Intent splashScreenIntent = new Intent(EmployeeHomeScreen.this, SplashActivity.class);
        startActivity(splashScreenIntent);
        finish();
    }

    private void populateSignedInUserProps() {
        signedInUserProps = AppPreferences.getSignedInUser(this);
        if (signedInUserProps != null) {
            Log.d("ResultTag", "Signed In User Props = " + signedInUserProps.toString());

            String lastName = signedInUserProps.optString(FinanceLearningConstants.LAST_NAME);
            String surname = signedInUserProps.optString(FinanceLearningConstants.SURNAME);
            String userId = signedInUserProps.optString("staff_id");

            if (userId.equals("guest")) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Welcome, Guest");
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(Html.fromHtml("Welcome, <b>" + surname + " " + lastName + "</b>"));
                }
            }
            boolean passwordUpdated = signedInUserProps.optBoolean(FinanceLearningConstants.PASSWORD_UPDATED, false);
            boolean isFirstLoggedIn = AppPreferences.isFirstTime();
            if (isFirstLoggedIn && !userId.equals("guest") && !passwordUpdated) {
                Intent updatePasswordIntent = new Intent(EmployeeHomeScreen.this, UpdatePasswordActivity.class);
                startActivity(updatePasswordIntent);
            }
        }
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        librariesOrVideoDemosTextView = (TextView) findViewById(R.id.libraries);
        myCoursesView = findViewById(R.id.my_courses_view);
        myCoursesView.setOnClickListener(this);
        View requestDemoView = findViewById(R.id.request_demo_view);
        requestDemoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String staffId = signedInUserProps.optString("staff_id");
                if (staffId.equals("guest")) {
                    Intent guestVideosIntent = new Intent(EmployeeHomeScreen.this, GuestVideosActivity.class);
                    startActivity(guestVideosIntent);
                } else {
                    Intent librariesIntent = new Intent(EmployeeHomeScreen.this, PDFLibrariesRecyclerActivity.class);
                    startActivity(librariesIntent);
                }
            }
        });
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

    private void clearPreviousAnswers() {
        try {
            FinanceLearningConstants.pretestResult.clear();
            FinanceLearningConstants.selectedAnOption.clear();
        } catch (Exception ignored) {

        }
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
                        startActivity(pretestResultIntent);
                    } else {
                        Intent preTestIntent = new Intent(EmployeeHomeScreen.this, PreTestCourseSelectionActivity.class);
                        startActivity(preTestIntent);
                    }
                } else {
                    Intent mainTestResultIntent = new Intent(EmployeeHomeScreen.this, MainTestResultActivity.class);
                    startActivity(mainTestResultIntent);
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

                GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                };

                HashMap<String, Object> courseProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                if (!listOfSelectedCourseIds.isEmpty() && courseProps != null) {
                    String courseId = (String) courseProps.get(FinanceLearningConstants.COURSE_ID);
                    if (listOfSelectedCourseIds.contains(courseId)) {
                        AppPreferences.saveCourse(courseId, GsonUtils.getHashMarshall(courseProps));
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

    public void fetchUpdatedUserInfo() {

        if (signedInUserProps != null) {

            FirebaseUtils.getStaffReference().child(signedInUserProps.optString("staff_id"))
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                                };
                                HashMap<String, Object> newUserProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                                if (newUserProps != null) {
                                    AppPreferences.saveLoggedInUser(EmployeeHomeScreen.this, newUserProps);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

        }

    }

}
