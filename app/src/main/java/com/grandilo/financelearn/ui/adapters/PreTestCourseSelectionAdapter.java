package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.PreTestCourseSelectionActivity;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.grandilo.financelearn.utils.FinanceLearningConstants.checkedPositions;

/**
 * @author Ugo
 */

public class PreTestCourseSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<HashMap<String, Object>> courses;

    private static JSONObject signedInUser;

    public PreTestCourseSelectionAdapter(Context context, List<HashMap<String, Object>> courses) {
        this.layoutInflater = LayoutInflater.from(context);
        this.courses = courses;
        signedInUser = AppPreferences.getSignedInUser(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.pre_test_course_item, parent, false);
        return new CourseItemHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseItemHolder courseItemHolder = (CourseItemHolder) holder;
        HashMap<String, Object> courseItem = courses.get(position);
        if (courseItem != null) {
            courseItemHolder.bindCourse(courseItem);
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

        public List<String> getCoursesAssignedList(JSONArray jsonArray) {
            List<String> assignedCoursesList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                assignedCoursesList.add(jsonArray.optString(i));
            }
            return assignedCoursesList;
        }

        void bindCourse(HashMap<String, Object> courseItem) {

            final String coursesAssigned = signedInUser.optString(FinanceLearningConstants.COURSES_ASSIGNED);
            final String courseId = (String) courseItem.get(FinanceLearningConstants.COURSE_ID);

            Log.d("CheckedCourses", "User Props=" + signedInUser.toString());

            JSONArray courseArray = null;
            if (coursesAssigned != null) {
                Log.d("CheckedCourses", "Course String =" + coursesAssigned);
                try {
                    courseArray = new JSONArray(coursesAssigned);
                    Log.d("CheckedCourses", courseArray.toString());
                    for (int i = 0; i < courseArray.length(); i++) {
                        String s = courseArray.optString(i);
                        if (s.equals(courseId) || FinanceLearningConstants.checkedPositions.get(courseId.hashCode())) {
                            if (!FinanceLearningConstants.idsOfCoursesToTest.contains(courseId)) {
                                FinanceLearningConstants.idsOfCoursesToTest.add(courseId);
                            }
                            checkBox.setChecked(true);
                            checkedPositions.put(courseId.hashCode(), true);
                            PreTestCourseSelectionActivity.reviewSelections();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("CheckedCourses", "Courses are null");
            }

            String courseItemTitle = (String) courseItem.get(FinanceLearningConstants.COURSE_NAME);
            courseItemTitleView.setText(courseItemTitle);

            Log.d("CheckedCourses", "Course Id = " + courseId + " And Course Name = " + courseItemTitle);

            final JSONArray finalCourseArray = courseArray;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (finalCourseArray != null && getCoursesAssignedList(finalCourseArray).contains(courseId)) {
                        checkBox.setChecked(true);
                    } else {
                        if (checked) {
                            if (!FinanceLearningConstants.idsOfCoursesToTest.contains(courseId) && FinanceLearningConstants.idsOfCoursesToTest.size() < 4) {
                                FinanceLearningConstants.idsOfCoursesToTest.add(courseId);
                                checkedPositions.put(courseId.hashCode(), true);
                            } else {
                                if (FinanceLearningConstants.idsOfCoursesToTest.contains(courseId)) {
                                    FinanceLearningConstants.idsOfCoursesToTest.remove(courseId);
                                }
                                checkedPositions.put(courseId.hashCode(), false);
                                checkBox.setChecked(false);
                            }
                        } else {
                            if (FinanceLearningConstants.idsOfCoursesToTest.contains(courseId)) {
                                FinanceLearningConstants.idsOfCoursesToTest.remove(courseId);
                                checkedPositions.put(courseId.hashCode(), false);
                            }
                        }
                    }
                    PreTestCourseSelectionActivity.reviewSelections();
                }

            });

        }

    }

}
