package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.PretestQuestionAndAnswersAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.UiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Ugo
 */

@SuppressWarnings("unchecked")
public class PretestQuestionsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionsPageCounter, previousQuestion, nextQuestion, questionsFetchProgressMessage;
    private ViewPager questionsViewPager;
    private PretestQuestionAndAnswersAdapter pretestQuestionAndAnswersAdapter;

    private List<String> selectedCoursesForPretest;
    private ChildEventListener questionsAndAnswersListener, coursesListener;
    private DatabaseReference preTestReference, coursesReference;

    private List<JSONObject> pretestQuestions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("PreTest");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        offloadIntent();

        initViews();

        pretestQuestionAndAnswersAdapter = new PretestQuestionAndAnswersAdapter(this, pretestQuestions);
        questionsViewPager.setAdapter(pretestQuestionAndAnswersAdapter);

        preTestReference = FirebaseUtils.getPretestReference();
        coursesReference = FirebaseUtils.getCourses();

        fetchQuestionsForSelectedCourses();
        fetchCourseNames();

        checkPretestStatus();
        questionsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    JSONObject previousQuestion = pretestQuestions.get(position - 1);
                    if (previousQuestion != null) {
                        String previousTestQuestion = previousQuestion.optString(FinanceLearningConstants.QUESTION);
                        if (previousTestQuestion != null && !FinanceLearningConstants.pickedOptions.containsKey(previousTestQuestion)) {
                            questionsViewPager.setCurrentItem(position - 1);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPretestStatus();
    }

    private void checkPretestStatus() {
        JSONObject signedInUser = AppPreferences.getSignedInUser(this);
        if (signedInUser != null) {
            boolean pretestTaken = signedInUser.optBoolean(FinanceLearningConstants.PRETEST_TAKEN,false);
            if (pretestTaken){
                finish();
            }
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

    private void offloadIntent() {
        Bundle intentExtras = getIntent().getExtras();
        selectedCoursesForPretest = intentExtras.getStringArrayList(FinanceLearningConstants.SELECTED_PRE_TEST_COURSES);
    }

    private void initViews() {
        questionsPageCounter = (TextView) findViewById(R.id.questions_counter);
        previousQuestion = (TextView) findViewById(R.id.previous_question);
        nextQuestion = (TextView) findViewById(R.id.next_question);
        questionsViewPager = (ViewPager) findViewById(R.id.pretest_viewpager);
        questionsFetchProgressMessage = (TextView) findViewById(R.id.questions_fetch_progress_message);
        previousQuestion.setOnClickListener(this);
        nextQuestion.setOnClickListener(this);
        questionsViewPager.setOffscreenPageLimit(pretestQuestions.size() - 1);
        questionsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    previousQuestion.setVisibility(View.GONE);
                } else {
                    previousQuestion.setVisibility(View.VISIBLE);
                    if (position == pretestQuestions.size() - 1) {
                        nextQuestion.setText(R.string.finish_up);
                    } else {
                        nextQuestion.setText(getString(R.string.action_next));
                    }
                }

                questionsPageCounter.setText(questionsViewPager.getCurrentItem() + 1 + " of " + pretestQuestions.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

    }

    private void fetchQuestionsForSelectedCourses() {
        questionsAndAnswersListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                questionsFetchProgressMessage.setVisibility(View.GONE);

                GenericTypeIndicator<Object> objectGenericTypeIndicator = new GenericTypeIndicator<Object>() {
                };

                HashMap<Integer, HashMap<String, Object>> pretestInfo = (HashMap<Integer, HashMap<String, Object>>) dataSnapshot.getValue(objectGenericTypeIndicator);

                if (pretestInfo != null) {

                    JSONObject preTestJSONObject = new JSONObject(pretestInfo);
                    String courseId = preTestJSONObject.optString(FinanceLearningConstants.COURSE_ID);
                    if (selectedCoursesForPretest.contains(courseId)) {
                        pretestQuestions.add(preTestJSONObject);
                        //NotifyDataSetChanged here
                        pretestQuestionAndAnswersAdapter.notifyDataSetChanged();
                    }

                }

                questionsPageCounter.setText(questionsViewPager.getCurrentItem() + 1 + " of " + pretestQuestions.size());
                nextQuestion.setVisibility(View.VISIBLE);

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

        preTestReference.addChildEventListener(questionsAndAnswersListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (preTestReference != null && questionsAndAnswersListener != null) {
            preTestReference.removeEventListener(questionsAndAnswersListener);
        }
        if (coursesReference != null && coursesListener != null) {
            coursesReference.removeEventListener(coursesListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_question:
                if (!nextQuestion.getText().toString().equals(getString(R.string.finish_up))) {
                    int nexItem = questionsViewPager.getCurrentItem() + 1;
                    if (nexItem <= pretestQuestions.size()) {
                        questionsViewPager.setCurrentItem(nexItem);
                    }
                } else {
                    //Finish up here
                    Intent preTestResultIntent = new Intent(PretestQuestionsActivity.this, PreTestResultActivity.class);
                    preTestResultIntent.putExtra(FinanceLearningConstants.TOTAL_NO_OF_QS, pretestQuestions.size());
                    startActivity(preTestResultIntent);
                }
                break;
            case R.id.previous_question:
                int previousItem = questionsViewPager.getCurrentItem() - 1;
                if (previousItem >= 0) {
                    questionsViewPager.setCurrentItem(previousItem);
                }
                break;
        }
    }

    private void fetchCourseNames() {

        coursesListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                };

                HashMap<String, Object> courseProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                String courseKey = dataSnapshot.getKey();

                if (selectedCoursesForPretest.contains(courseKey)) {
                    if (courseProps != null) {
                        String courseName = (String) courseProps.get(FinanceLearningConstants.COURSE_NAME);
                        FinanceLearningConstants.courseIdNameMap.put(dataSnapshot.getKey(), courseName);
                        FinanceLearningConstants.fullCourseDetailsMap.put(dataSnapshot.getKey(), courseProps);

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

        coursesReference.addChildEventListener(coursesListener);
    }

}
