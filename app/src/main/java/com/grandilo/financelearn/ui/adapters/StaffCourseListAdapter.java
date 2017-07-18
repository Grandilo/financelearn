package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.StaffProfileManagementScreen;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class StaffCourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<HashMap<String, String>> courses;
    private Context context;

    public StaffCourseListAdapter(Context context, List<HashMap<String, String>> courses) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.courses = courses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.pre_test_course_item, parent, false);
        return new CourseItemHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseItemHolder courseItemHolder = (CourseItemHolder) holder;
        HashMap<String, String> courseItem = courses.get(position);
        if (courseItem != null) {
            courseItemHolder.bindCourse(context, courseItem);
        }
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    @SuppressWarnings({"WeakerAccess", "RedundantCast"})
    static class CourseItemHolder extends RecyclerView.ViewHolder {

        private TextView courseItemTitleView;
        private CheckBox checkBox;

        public CourseItemHolder(View itemView) {
            super(itemView);
            courseItemTitleView = (TextView) itemView.findViewById(R.id.course_title);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        void bindCourse(final Context context, HashMap<String, String> courseItem) {

            final String courseId = courseItem.get(FinanceLearningConstants.COURSE_ID);
            String courseItemTitle = courseItem.get(FinanceLearningConstants.COURSE_NAME);
            courseItemTitleView.setText(courseItemTitle);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        if (FinanceLearningConstants.coursesToAssign.size() < 2) {
                            if (!FinanceLearningConstants.coursesToAssign.contains(courseId)) {
                                FinanceLearningConstants.coursesToAssign.add(courseId);
                            }
                        } else {
                            checkBox.setChecked(false);
                            Toast.makeText(context, "The 2 courses have being selected", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (FinanceLearningConstants.coursesToAssign.contains(courseId)) {
                            FinanceLearningConstants.coursesToAssign.remove(courseId);
                        }
                    }
                    StaffProfileManagementScreen.reviewSelection();
                }

            });

        }

    }

}
