package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.MainTestResultActivity;
import com.grandilo.financelearn.ui.activities.PreTestResultActivity;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.GsonUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Ugo
 */

public class TestResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public TestResultAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.test_result_item, parent, false);
        return new PretestResultViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PretestResultViewHolder pretestResultViewHolder = (PretestResultViewHolder) holder;
        String courseId = FinanceLearningConstants.idsOfCoursesToTest.get(position);
        pretestResultViewHolder.bindResult(context, courseId);
    }

    @Override
    public int getItemCount() {
        return FinanceLearningConstants.idsOfCoursesToTest.size();
    }

    @SuppressWarnings("WeakerAccess")
    static class PretestResultViewHolder extends RecyclerView.ViewHolder {

        TextView resultItem, percentageView;

        public PretestResultViewHolder(View itemView) {
            super(itemView);
            resultItem = itemView.findViewById(R.id.course_title);
            percentageView = itemView.findViewById(R.id.percentage_score);
        }

        public String getResultCategory(Context context, float percentage, boolean format) {
            if (percentage < 39) {
                if (format) {
                    return context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_BASIC : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_BELOW_AVERAGE;
                } else {
                    return FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_BASIC;
                }
            } else if (percentage > 39 && percentage < 69) {
                if (format) {
                    return context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_INTERMEDIATE : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_AVERAGE;
                } else {
                    return FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_INTERMEDIATE;
                }
            } else {
                if (format) {
                    return context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_EXPERT : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_GOOD;
                } else {
                    return FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_EXPERT;
                }
            }
        }

        public void bindResult(Context context, String courseId) {

            try {

                JSONObject resultObject;
                if (context instanceof PreTestResultActivity) {
                    resultObject = (JSONObject) FinanceLearningConstants.pretestResult.get(courseId);
                } else {
                    resultObject = (JSONObject) FinanceLearningConstants.mainTestResult.get(courseId);
                }

                resultItem.setText(GsonUtils.getCourseName(courseId));

                if (resultObject != null) {
                    int selectedOptions = resultObject.length();
                    float percentAge = (100 * selectedOptions) / 5;
                    FinanceLearningConstants.mainTestScoresMap.put(courseId, percentAge);
                    if (selectedOptions > 0) {
                        percentageView.setText(percentAge + " % (" + getResultCategory(context, percentAge, true).replace("_", " ") + ")");
                        if (getResultCategory(context, percentAge, true).contains(context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_BASIC : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_BELOW_AVERAGE)) {
                            percentageView.setTextColor(Color.RED);
                        } else if (getResultCategory(context, percentAge, true).contains(context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_INTERMEDIATE : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_AVERAGE)) {
                            percentageView.setTextColor(Color.BLUE);
                        } else {
                            percentageView.setTextColor(Color.GREEN);
                        }
                    } else {
                        percentageView.setText("0 % (" + (context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_BASIC.replace("_", " ") : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_BELOW_AVERAGE.replace("_", " ")) + ")");
                        percentageView.setTextColor(Color.RED);
                    }
                } else {
                    percentageView.setText("0 % (" + (context instanceof PreTestResultActivity ? FinanceLearningConstants.PRETEST_PROFICIENCY_LEVEL_BASIC.replace("_", " ") : FinanceLearningConstants.MAIN_PROFICIENCY_LEVEL_BELOW_AVERAGE.replace("_", " ")) + ")");
                    percentageView.setTextColor(Color.RED);
                }

                injectCourseRecommendations(context, courseId);

            } catch (NullPointerException ignore) {

            }

        }

        private void injectCourseRecommendations(Context context, String courseId) {

            if (context instanceof MainTestResultActivity) {

                JSONObject pretestResultsObject = (JSONObject) FinanceLearningConstants.pretestResult.get(courseId);

                JSONObject mainTestResultsObject = (JSONObject) FinanceLearningConstants.mainTestResult.get(courseId);

                ArrayList<String> recommendationKeys = new ArrayList<>();

                String pretestCategory;

                if (pretestResultsObject != null) {
                    float pretestSelectedOptions = pretestResultsObject.length() * 100 / 5;
                    pretestCategory = getResultCategory(context, pretestSelectedOptions, false);
                } else {
                    pretestCategory = getResultCategory(context, 0, false);
                }

                String mainTestCategory;
                if (mainTestResultsObject != null) {
                    float mainTestRightAnswer = mainTestResultsObject.length() * 100 / 5;
                    mainTestCategory = getResultCategory(context, mainTestRightAnswer, true);
                } else {
                    mainTestCategory = getResultCategory(context, 0, true);
                }

                recommendationKeys.add(pretestCategory);
                recommendationKeys.add(mainTestCategory);

                FinanceLearningConstants.recommendationsMap.put(courseId, recommendationKeys);

            }

        }

    }

}
