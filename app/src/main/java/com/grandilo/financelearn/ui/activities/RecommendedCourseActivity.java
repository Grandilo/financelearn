package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings("deprecation")
public class RecommendedCourseActivity extends AppCompatActivity {

    private List<Float> scores = new ArrayList<>();
    private Float maxScore;
    private String recommendedCourse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView finalResultStatement = (TextView) findViewById(R.id.recommendations_result_view);
        extractScores();
        getMaxScore();
        fetchMaxScoreCourse();
        finalResultStatement.setText(Html.fromHtml("From your test results, we think you\'ll make a good staff in <b>" + FinanceLearningConstants.courseMap.get(recommendedCourse) + "</b>"));
    }

    private void fetchMaxScoreCourse() {
        for (String key : FinanceLearningConstants.mainTestScoresMap.keySet()) {
            Float value = FinanceLearningConstants.mainTestScoresMap.get(key);
            if (value.equals(maxScore)) {
                recommendedCourse = key;
            }
        }
    }

    private void extractScores() {
        for (String key : FinanceLearningConstants.mainTestScoresMap.keySet()) {
            scores.add(FinanceLearningConstants.mainTestScoresMap.get(key));
        }
    }

    private void getMaxScore() {
        maxScore = Collections.max(scores);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
