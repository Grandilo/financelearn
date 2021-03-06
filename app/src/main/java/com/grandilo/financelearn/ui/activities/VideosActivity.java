package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.VideosAdapter;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings("unchecked")
public class VideosActivity extends AppCompatActivity {

    private List<HashMap<String, Object>> videos = new ArrayList<>();
    private VideosAdapter videosAdapter;

    private ChildEventListener videosListener;
    private DatabaseReference videosReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView videosRecyclerView = (RecyclerView) findViewById(R.id.videos_recycler_view);
        TextView proceedToMainTestView = (TextView) findViewById(R.id.proceed_to_main_test);

        videosAdapter = new VideosAdapter(this, videos);
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        videosRecyclerView.setAdapter(videosAdapter);

        videosReference = FirebaseUtils.getCourseVideosReference();

        fetchVideos();

        proceedToMainTestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainTestIntent = new Intent(VideosActivity.this, MainTestQuestionsActivity.class);
                startActivity(mainTestIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getResultCategory(float percentage) {
        if (percentage < 39) {
            return "Basic";
        } else if (percentage > 39 && percentage < 69) {
            return "Intermediate";
        } else {
            return "Expert";
        }
    }

    //Fetch Videos based on the scores of the staff in the pretest
    private void fetchVideos() {

        videosListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null) {

                    GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                    };

                    HashMap<String, Object> videoProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                    if (videoProps != null) {

                        HashMap<String, Object> courseDetails = (HashMap<String, Object>) videoProps.get(FinanceLearningConstants.COURSE_DETAILS);
                        String courseId = (String) courseDetails.get(FinanceLearningConstants.COURSE_ID);
                        final String expertLevel = (String) videoProps.get(FinanceLearningConstants.EXPERTISE_LEVEL);

                        if (FinanceLearningConstants.idsOfCoursesToTest.contains(courseId)) {
                            JSONObject pretestResultObject = (JSONObject) FinanceLearningConstants.pretestResult.get(courseId);
                            if (pretestResultObject != null) {
                                int rightAnswersCount = pretestResultObject.length();
                                float percentAge = 100 * rightAnswersCount / 5;
                                Log.d("ResultLog", "RightAnswersCount=" + rightAnswersCount + " percentage=" + percentAge);

                                if (rightAnswersCount > 0) {
                                    if (getResultCategory(percentAge).contains(expertLevel)) {
                                        videos.add(videoProps);
                                    }
                                } else {
                                    if (expertLevel.contains("Basic")) {
                                        videos.add(videoProps);
                                    }
                                }
                            }
                        }

                        videosAdapter.notifyDataSetChanged();
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

        videosReference.addChildEventListener(videosListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (videosListener != null && videosReference != null) {
            videosReference.removeEventListener(videosListener);
        }

    }

}
