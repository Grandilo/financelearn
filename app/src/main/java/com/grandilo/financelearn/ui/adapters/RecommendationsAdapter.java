package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ugo
 */
public class RecommendationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> recommendations = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public RecommendationsAdapter(Context context, List<String> recommendations) {
        this.recommendations = recommendations;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.recommendations_item, parent, false);
        return new RecommendationsHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecommendationsHolder recommendationsHolder = (RecommendationsHolder) holder;
        String courseTitle = recommendations.get(position);
        recommendationsHolder.bindData(courseTitle);
    }

    @Override
    public int getItemCount() {
        return recommendations != null ? recommendations.size() : 0;
    }

    @SuppressWarnings({"WeakerAccess", "RedundantCast"})
    static class RecommendationsHolder extends RecyclerView.ViewHolder {

        private TextView courseTitleView;

        public RecommendationsHolder(View itemView) {
            super(itemView);
            courseTitleView = (TextView) itemView.findViewById(R.id.recommended_course_title);
        }

        void bindData(String recommendedTitle) {
            courseTitleView.setText(recommendedTitle);
        }

    }

}
