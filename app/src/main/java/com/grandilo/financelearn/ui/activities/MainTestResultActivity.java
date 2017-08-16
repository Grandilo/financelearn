package com.grandilo.financelearn.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.TestResultAdapter;
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

public class MainTestResultActivity extends AppCompatActivity {

    private RecyclerView courseResultList;
    private List<String> courseIds = new ArrayList<>();
    private int totalNumberOfQs;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        for (String key : FinanceLearningConstants.courseIdNameMap.keySet()) {
            courseIds.add(key);
        }

        Log.d("ResultTag", "CourseIds = " + TextUtils.join(",", courseIds));

        courseResultList = (RecyclerView) findViewById(R.id.course_result_list);
        TextView continueButton = (TextView) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent recommendationsIntent = new Intent(MainTestResultActivity.this, RecommendedCourseActivity.class);
                startActivity(recommendationsIntent);
                finish();
            }

        });
        totalNumberOfQs = getIntent().getExtras().getInt(FinanceLearningConstants.TOTAL_NO_OF_QS);
        initResultAdapter();

        updateSignedInUserMainTestState();
        FinanceLearningConstants.pickedOptions.clear();
    }

    private void updateSignedInUserMainTestState() {

        JSONObject jsonObject = AppPreferences.getSignedInUser(this);

        if (jsonObject != null) {

            final String staffId = jsonObject.optString("staff_id");

            HashMap<String, Object> updatableProps = new HashMap<>();

            if (!FinanceLearningConstants.mainTestRightAnswers.isEmpty() || !FinanceLearningConstants.mainTestWrongAnswers.isEmpty()) {

                if (!FinanceLearningConstants.mainTestRightAnswers.isEmpty()) {
                    JSONObject rightAnsJSON = new JSONObject(FinanceLearningConstants.mainTestRightAnswers);
                    updatableProps.put(FinanceLearningConstants.MAIN_TEST_RIGHT_ANSWERS, rightAnsJSON.toString());
                }

                if (!FinanceLearningConstants.mainTestWrongAnswers.isEmpty()) {
                    JSONObject wrongAnsJSON = new JSONObject(FinanceLearningConstants.mainTestWrongAnswers);
                    updatableProps.put(FinanceLearningConstants.MAIN_TEST_WRONG_ANSWERS, wrongAnsJSON.toString());
                }

                updatableProps.put(FinanceLearningConstants.TOTAL_NO_OF_QS, totalNumberOfQs);

            }

            FirebaseUtils.getStaffReference().child(staffId).updateChildren(updatableProps, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError == null) {

                        FirebaseUtils.getStaffReference().child(staffId).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot != null) {

                                    GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                                    };

                                    HashMap<String, Object> newUserProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                                    if (newUserProps != null) {
                                        AppPreferences.saveLoggedInUser(MainTestResultActivity.this, newUserProps);
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                    }

                }

            });

        }

    }

    private void resetTest() {
        JSONObject signedInUserProps = AppPreferences.getSignedInUser(this);
        if (signedInUserProps != null) {
            HashMap<String, Object> updatableProps = new HashMap<>();

            updatableProps.put(FinanceLearningConstants.PRETEST_TAKEN, null);
            updatableProps.put(FinanceLearningConstants.PRETEST_RIGHT_ANSWERS, null);
            updatableProps.put(FinanceLearningConstants.PRETEST_WRONG_ANSWERS, null);

            updatableProps.put(FinanceLearningConstants.MAIN_TEST_TAKEN, null);
            updatableProps.put(FinanceLearningConstants.MAIN_TEST_WRONG_ANSWERS, null);
            updatableProps.put(FinanceLearningConstants.MAIN_TEST_RIGHT_ANSWERS, null);

            updatableProps.put(FinanceLearningConstants.TOTAL_NO_OF_QS, null);
            updatableProps.put(FinanceLearningConstants.ALL_PRETEST_COURSES, null);

            progressDialog.setMessage("Preparing for retake, please wait...");
            progressDialog.show();

            FirebaseUtils.getStaffReference().child(signedInUserProps.optString("staff_id")).updateChildren(updatableProps, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    progressDialog.dismiss();
                    if (databaseError == null) {
                        Toast.makeText(MainTestResultActivity.this, "You can retake the test.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainTestResultActivity.this, "Error performing retake. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initResultAdapter() {
        TestResultAdapter testResultAdapter = new TestResultAdapter(this, courseIds, totalNumberOfQs);
        courseResultList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        courseResultList.setAdapter(testResultAdapter);
    }

}
