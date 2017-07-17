package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * @author Ugo
 */

public class PreTestCourseSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<HashMap<String, String>> courses;
    private Context context;

    private static JSONObject signedInUser;
    private static ArrayList<String> coursesToTest = new ArrayList<>();

    public PreTestCourseSelectionAdapter(Context context, List<HashMap<String, String>> courses) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.courses = courses;
        signedInUser = AppPreferences.getSignedInUser(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.pre_test_course_item, parent, false);
        return new CourseItemHolder(convertView);
    }

    public ArrayList<String> getCoursesToTest() {
        return coursesToTest;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseItemHolder courseItemHolder = (CourseItemHolder) holder;
        HashMap<String, String> courseItem = courses.get(position);
        if (courseItem != null) {
            courseItemHolder.bindCourse(context, courseItem, position);
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

        private SparseBooleanArray checkedPositions = new SparseBooleanArray();

        public CourseItemHolder(View itemView) {
            super(itemView);
            courseItemTitleView = (TextView) itemView.findViewById(R.id.course_title);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        void bindCourse(final Context context, HashMap<String, String> courseItem, final int position) {

            final String courseAssigned = signedInUser.optString(FinanceLearningConstants.COURSES_ASSIGNED);
            final String courseId = courseItem.get(FinanceLearningConstants.COURSE_ID);
            Log.d("CheckedCourses", "User Props=" + signedInUser.toString());
            if (courseAssigned != null) {
                Log.d("CheckedCourses", "Course String =" + courseAssigned);
                try {
                    JSONArray courseArray = new JSONArray(courseAssigned);
                    Log.d("CheckedCourses", courseArray.toString());
                    for (int i = 0; i < courseArray.length(); i++) {
                        String s = courseArray.optString(i);
                        if (s.equals(courseId)) {
                            checkBox.setChecked(true);
                            checkedPositions.put(courseId.hashCode(), true);
                            coursesToTest.add(courseId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("CheckedCourses", "Courses are null");
            }

            String courseItemTitle = courseItem.get(FinanceLearningConstants.COURSE_NAME);
            courseItemTitleView.setText(courseItemTitle);
            Log.d("CheckedCourses", "Course Id = " + courseId);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                    if (checkedPositions.get(courseId.hashCode())) {
                        checkBox.setChecked(true);
                    } else {
                        if (checked) {
                            if (coursesToTest.size() < 4) {
                                if (!coursesToTest.contains(courseId)) {
                                    coursesToTest.add(courseId);
                                }
                            } else {
                                checkBox.setChecked(false);
                                Toast.makeText(context, "All 4 courses have being selected. You may proceed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (coursesToTest.contains(courseId)) {
                                coursesToTest.remove(courseId);
                            }
                        }
                    }

                    PreTestCourseSelectionActivity.reviewSelections();

                }

            });

        }

    }

}
