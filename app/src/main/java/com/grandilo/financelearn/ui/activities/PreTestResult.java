package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.PreTestResultAdapter;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ugo.
 */

public class PreTestResult extends AppCompatActivity {

    private RecyclerView courseResultList;
    private List<String> courseIds = new ArrayList<>();
    private int totalNumberOfQs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretest_result);
        for (String key : FinanceLearningConstants.courseMap.keySet()) {
            courseIds.add(key);
        }

        Log.d("ResultTag","CourseIds = "+ TextUtils.join(",",courseIds));

        courseResultList = (RecyclerView) findViewById(R.id.course_result_list);
        totalNumberOfQs = getIntent().getExtras().getInt(FinanceLearningConstants.TOTAL_NO_OF_QS);
        initResultAdapter();
    }

    private void initResultAdapter() {
        PreTestResultAdapter preTestResultAdapter = new PreTestResultAdapter(this, courseIds, totalNumberOfQs);
        courseResultList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        courseResultList.setAdapter(preTestResultAdapter);
    }

}
