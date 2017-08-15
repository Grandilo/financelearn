package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.MainTestQuestionAndAnswersAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings("unchecked")
public class MainTestQuestionsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionsPageCounter, previousQuestion, nextQuestion, questionsFetchProgressMessage;
    private ViewPager questionsViewPager;
    private MainTestQuestionAndAnswersAdapter mainTestQuestionAndAnswersAdapter;

    private List<String> selectedCoursesForMainTest = new ArrayList<>();

    private ChildEventListener questionsAndAnswersListener, coursesListener;
    private DatabaseReference mainTestReference, coursesReference;

    private List<JSONObject> mainTestQuestions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Main Test");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupCourseList();

        initViews();

        mainTestQuestionAndAnswersAdapter = new MainTestQuestionAndAnswersAdapter(this, mainTestQuestions);
        questionsViewPager.setAdapter(mainTestQuestionAndAnswersAdapter);

        mainTestReference = FirebaseUtils.getMainTestReference();
        coursesReference = FirebaseUtils.getCourses();

        fetchQuestionsForSelectedCourses();
        fetchCourseNames();

        checkMainTestStatus();
        questionsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    JSONObject previousQuestion = mainTestQuestions.get(position - 1);
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
        checkMainTestStatus();
    }

    private void checkMainTestStatus() {
        JSONObject signedInUser = AppPreferences.getSignedInUser(this);
        if (signedInUser != null) {
            boolean mainTestTaken = signedInUser.optBoolean(FinanceLearningConstants.MAIN_TEST_TAKEN, false);
            if (mainTestTaken) {
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

    private void setupCourseList() {
        JSONObject signedInUser = AppPreferences.getSignedInUser(this);
        if (signedInUser != null) {
            String allTestCourses = signedInUser.optString(FinanceLearningConstants.ALL_PRETEST_COURSES);
            if (allTestCourses != null) {
                try {
                    JSONArray coursesArray = new JSONArray(allTestCourses);
                    for (int i = 0; i < coursesArray.length(); i++) {
                        String courseId = coursesArray.optString(i);
                        if (!selectedCoursesForMainTest.contains(courseId)) {
                            selectedCoursesForMainTest.add(courseId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initViews() {
        questionsPageCounter = (TextView) findViewById(R.id.questions_counter);
        previousQuestion = (TextView) findViewById(R.id.previous_question);
        nextQuestion = (TextView) findViewById(R.id.next_question);
        questionsViewPager = (ViewPager) findViewById(R.id.main_test_viewpager);
        questionsFetchProgressMessage = (TextView) findViewById(R.id.questions_fetch_progress_message);
        previousQuestion.setOnClickListener(this);
        nextQuestion.setOnClickListener(this);
        questionsViewPager.setOffscreenPageLimit(mainTestQuestions.size() - 1);
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
                    if (position == mainTestQuestions.size() - 1) {
                        nextQuestion.setText(R.string.finish_up);
                    } else {
                        nextQuestion.setText(getString(R.string.action_next));
                    }
                }

                questionsPageCounter.setText(questionsViewPager.getCurrentItem() + 1 + " of " + mainTestQuestions.size());

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
                    if (selectedCoursesForMainTest.contains(courseId)) {
                        mainTestQuestions.add(preTestJSONObject);
                        //NotifyDataSetChanged here
                        mainTestQuestionAndAnswersAdapter.notifyDataSetChanged();
                    }

                }

                questionsPageCounter.setText(questionsViewPager.getCurrentItem() + 1 + " of " + mainTestQuestions.size());
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

        mainTestReference.addChildEventListener(questionsAndAnswersListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mainTestReference != null && questionsAndAnswersListener != null) {
            mainTestReference.removeEventListener(questionsAndAnswersListener);
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
                    if (nexItem <= mainTestQuestions.size()) {
                        questionsViewPager.setCurrentItem(nexItem);
                    }
                } else {
                    //Finish up here
                    Intent preTestResultIntent = new Intent(MainTestQuestionsActivity.this, MainTestResultActivity.class);
                    preTestResultIntent.putExtra(FinanceLearningConstants.TOTAL_NO_OF_QS, mainTestQuestions.size());
                    startActivity(preTestResultIntent);
                    finish();
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

                if (selectedCoursesForMainTest.contains(courseKey)) {
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
