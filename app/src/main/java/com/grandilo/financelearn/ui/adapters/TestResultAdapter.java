package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.PreTestResultActivity;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import org.json.JSONObject;

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

        public String getResultCategory(float percentage) {
            if (percentage < 39) {
                return "Basic";
            } else if (percentage > 39 && percentage < 69) {
                return "Intermediate";
            } else {
                return "Expert";
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

                resultItem.setText(FinanceLearningConstants.courseMap.get(courseId));

                if (rightAnswers != null) {
                    int rightAnswersCount = rightAnswers.size();
                    float percentAge = rightAnswers.size() * 100 / totalNoOfQ;
                    FinanceLearningConstants.mainTestScoresMap.put(courseId, percentAge);
                    if (rightAnswersCount > 0) {
                        percentageView.setText(percentAge + " % (" + getResultCategory(percentAge) + ")");
                        if (getResultCategory(percentAge).contains("Basic")) {
                            percentageView.setTextColor(Color.RED);
                        } else if (getResultCategory(percentAge).contains("Intermediate")) {
                            percentageView.setTextColor(Color.BLUE);
                        } else {
                            percentageView.setTextColor(Color.GREEN);
                        }
                    } else {
                        percentageView.setText("0 % (Basic)");
                        percentageView.setTextColor(Color.RED);
                    }
                } else {
                    percentageView.setText("0 % (Basic)");
                    percentageView.setTextColor(Color.RED);
                }

            } catch (NullPointerException ignore) {

            }

        }

    }

}
