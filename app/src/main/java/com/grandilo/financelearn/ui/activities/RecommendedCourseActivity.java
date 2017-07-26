package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.RecommendationsAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.EmailUtils;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings({"deprecation", "unchecked"})
public class RecommendedCourseActivity extends AppCompatActivity {

    private RecyclerView recommendedCoursesRecyclerView;

    private List<String> recommendedCourses = new ArrayList<>();

    private HashMap<String, Object> updatableUserProps = new HashMap<>();

    private JSONObject signedInUserProps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        signedInUserProps = AppPreferences.getSignedInUser(this);
        recommendedCoursesRecyclerView = (RecyclerView) findViewById(R.id.recommended_courses_recycler_view);
        initRecommendations();
        initRecommendationsAdapter();
    }

    private void initRecommendations() {
        for (String key : FinanceLearningConstants.recommendationsMap.keySet()) {

            List<String> recommendationList = FinanceLearningConstants.recommendationsMap.get(key);

            if (recommendationList != null) {
                String stringifiedList = TextUtils.join("_", recommendationList);
                if (stringifiedList != null) {
                    HashMap<String, Object> associatedCourse = FinanceLearningConstants.fullCourseDetailsMap.get(key);
                    if (associatedCourse != null) {

                        HashMap<String, String> recommendationsMap = (HashMap<String, String>) associatedCourse.get("recommendation");

                        if (recommendationsMap != null) {
                            String recommendation = recommendationsMap.get(stringifiedList);
                            if (recommendation != null) {
                                if (!recommendation.contains("go_back_to_videos")) {
                                    if (!recommendedCourses.contains(recommendation)) {
                                        recommendedCourses.add(recommendation);
                                    }
                                } else {
                                    updatableUserProps.put("go_back_to_videos", "go_back_to_videos");
                                    Toast.makeText(RecommendedCourseActivity.this, "You didn't do well in some courses. Please revisit the videos", Toast.LENGTH_LONG).show();
                                    Intent videosIntent = new Intent(RecommendedCourseActivity.this, VideosActivity.class);
                                    startActivity(videosIntent);
                                    finish();
                                }
                            }
                        }
                    }
                }
            }
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!updatableUserProps.containsKey("go_back_to_videos")) {
                    updateUserState();
                }
            }

        }, 500);


        String staffHrId = signedInUserProps.optString(FinanceLearningConstants.STAFF_HR_ID);
        String staffManagerId = signedInUserProps.optString(FinanceLearningConstants.STAFF_MANAGER_ID);

        if (!TextUtils.isEmpty(staffHrId)) {
            FirebaseUtils.getStaffReference().orderByChild(staffHrId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };

                        HashMap<String, Object> hrProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                        if (hrProps != null) {
                            String email = (String) hrProps.get("email");
                            if (email != null) {
                                if (!AppPreferences.hasEmailBeingSent(email)) {
                                    EmailUtils.sendEmail(RecommendedCourseActivity.this, email, recommendedCourses);
                                }
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (!TextUtils.isEmpty(staffManagerId)) {

            FirebaseUtils.getStaffReference().orderByChild(staffManagerId).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };

                        HashMap<String, Object> managerProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                        if (managerProps != null) {
                            String email = (String) managerProps.get("email");
                            if (email != null) {
                                if (!AppPreferences.hasEmailBeingSent(email)) {
                                    EmailUtils.sendEmail(RecommendedCourseActivity.this, email, recommendedCourses);
                                }
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecommendationsAdapter() {
        RecommendationsAdapter recommendationsAdapter = new RecommendationsAdapter(this, recommendedCourses);
        recommendedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recommendedCoursesRecyclerView.setAdapter(recommendationsAdapter);
    }

    private void updateUserState() {
        updatableUserProps.put(FinanceLearningConstants.MAIN_TEST_TAKEN, true);
        FirebaseUtils.getStaffReference().child(signedInUserProps.optString("staff_id")).updateChildren(updatableUserProps, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {

                    FirebaseUtils.getStaffReference().child(signedInUserProps.optString("staff_id")).
                            addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot != null) {

                                        GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                                        };

                                        HashMap<String, Object> newUserProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                                        if (newUserProps != null) {
                                            AppPreferences.saveLoggedInUser(RecommendedCourseActivity.this, newUserProps);
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
