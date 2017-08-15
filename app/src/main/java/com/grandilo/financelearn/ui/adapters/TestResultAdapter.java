package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.MainTestResultActivity;
import com.grandilo.financelearn.ui.activities.PreTestResultActivity;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.UiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ugo
 */

public class TestResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> courseIds;
    private int totalNumberOfQuestions;

    public TestResultAdapter(Context context, List<String> courseIds, int totalNumberOfQuestions) {
        this.context = context;
        this.totalNumberOfQuestions = totalNumberOfQuestions;
        this.courseIds = courseIds;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.test_result_item, parent, false);
        return new PretestResultViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PretestResultViewHolder pretestResultViewHolder = (PretestResultViewHolder) holder;
        String courseId = courseIds.get(position);
        pretestResultViewHolder.bindResult(context, totalNumberOfQuestions, courseId);
    }

    @Override
    public int getItemCount() {
        return courseIds.size();
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

        public void bindResult(Context context, int totalNoOfQ, String courseId) {

            try {

                List<JSONObject> rightAnswers;

                if (context instanceof PreTestResultActivity) {
                    rightAnswers = FinanceLearningConstants.pretestRightAnswers.get(courseId);
                } else {
                    rightAnswers = FinanceLearningConstants.mainTestRightAnswers.get(courseId);
                }

                resultItem.setText(FinanceLearningConstants.courseIdNameMap.get(courseId));

                if (rightAnswers != null) {
                    int rightAnswersCount = rightAnswers.size();
                    float percentAge = (100 * rightAnswers.size()) / 5;
                    FinanceLearningConstants.mainTestScoresMap.put(courseId, percentAge);
                    if (rightAnswersCount > 0) {
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

                injectCourseRecommendations(context, courseId, totalNoOfQ);

            } catch (NullPointerException ignore) {

            }

        }

        private void injectCourseRecommendations(Context context, String courseId, int totalNoOfQ) {

            if (context instanceof MainTestResultActivity) {

                List<JSONObject> pretestRightAnswersList = FinanceLearningConstants.pretestRightAnswers.get(courseId);
                List<JSONObject> mainTestRightAnswers = FinanceLearningConstants.mainTestRightAnswers.get(courseId);

                ArrayList<String> recommendationKeys = new ArrayList<>();

                String pretestCategory;

                if (pretestRightAnswersList != null) {
                    float pretestRightAnswer = pretestRightAnswersList.size() * 100 / totalNoOfQ;
                    pretestCategory = getResultCategory(context, pretestRightAnswer, false);
                } else {
                    pretestCategory = getResultCategory(context, 0, false);
                }

                String mainTestCategory;
                if (mainTestRightAnswers != null) {
                    float mainTestRightAnswer = mainTestRightAnswers.size() * 100 / totalNoOfQ;
                    mainTestCategory = getResultCategory(context, mainTestRightAnswer, true);
                } else {
                    mainTestCategory = getResultCategory(context, 0, true);
                }

                recommendationKeys.add(pretestCategory);
                recommendationKeys.add(mainTestCategory);

                FinanceLearningConstants.recommendationsMap.put(courseId, recommendationKeys);

                Log.d("ListJoin", "CourseId= " + courseId + "and rec key=" + TextUtils.join(",", recommendationKeys));

            }

        }

    }

}
