package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Ugo
 */

public class PreTestResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> courseIds;
    private int totalNumberOfQuestions;

    public PreTestResultAdapter(Context context, List<String> courseIds, int totalNumberOfQuestions) {
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
        pretestResultViewHolder.bindResult(totalNumberOfQuestions, courseId);
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
            resultItem = (TextView) itemView.findViewById(R.id.course_title);
            percentageView = (TextView) itemView.findViewById(R.id.percentage_score);
        }

        public void bindResult(int totalNoOfQ, String courseId) {
            try {
                List<JSONObject> rightAnswers = FinanceLearningConstants.rightAnswersMap.get(courseId);
                resultItem.setText(FinanceLearningConstants.courseMap.get(courseId));
                percentageView.setText(rightAnswers.size() * 100 / totalNoOfQ + " % ");
            }catch (NullPointerException ignore){

            }

        }

    }

}
