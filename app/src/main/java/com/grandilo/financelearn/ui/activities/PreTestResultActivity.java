package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo.
 */

public class PreTestResultActivity extends AppCompatActivity {

    private RecyclerView courseResultList;
    private List<String> courseIds = new ArrayList<>();
    private int totalNumberOfQs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretest_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        for (String key : FinanceLearningConstants.courseIdNameMap.keySet()) {
            if (!courseIds.contains(key)) {
                courseIds.add(key);
            }
        }

        Log.d("ResultTag", "CourseIds = " + TextUtils.join(",", courseIds));

        courseResultList = (RecyclerView) findViewById(R.id.course_result_list);
        TextView continueButton = (TextView) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videosIntent = new Intent(PreTestResultActivity.this, VideosActivity.class);
                startActivity(videosIntent);
            }
        });
        totalNumberOfQs = getIntent().getExtras().getInt(FinanceLearningConstants.TOTAL_NO_OF_QS);
        initResultAdapter();
        updateSignedInUserPretestState();
    }

    private void updateSignedInUserPretestState() {

        JSONObject jsonObject = AppPreferences.getSignedInUser(this);

        if (jsonObject != null) {

            final String staffId = jsonObject.optString("staff_id");

            HashMap<String, Object> updatableProps = new HashMap<>();

            if (!FinanceLearningConstants.pretestRightAnswers.isEmpty() || !FinanceLearningConstants.pretestWrongAnswers.isEmpty()) {

                if (!FinanceLearningConstants.pretestRightAnswers.isEmpty()) {
                    JSONObject rightAnsJSON = new JSONObject(FinanceLearningConstants.pretestRightAnswers);
                    updatableProps.put(FinanceLearningConstants.PRETEST_RIGHT_ANSWERS, rightAnsJSON.toString());
                }

                if (!FinanceLearningConstants.pretestWrongAnswers.isEmpty()) {
                    JSONObject wrongAnsJSON = new JSONObject(FinanceLearningConstants.pretestWrongAnswers);
                    updatableProps.put(FinanceLearningConstants.PRETEST_WRONG_ANSWERS, wrongAnsJSON.toString());
                }

                updatableProps.put(FinanceLearningConstants.PRETEST_TAKEN, true);

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
                                        AppPreferences.saveLoggedInUser(PreTestResultActivity.this, newUserProps);
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
