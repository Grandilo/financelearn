package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class CourseSelectionSpinnerAdapter extends ArrayAdapter<HashMap<String, String>> {

    private LayoutInflater layoutInflater;

    public CourseSelectionSpinnerAdapter(@NonNull Context context, List<HashMap<String, String>> courses) {
        super(context, 0, courses);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SpinnerItemHolder spinnerItemHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_item, parent, false);
            spinnerItemHolder = new SpinnerItemHolder(convertView);
            convertView.setTag(spinnerItemHolder);
        } else {
            spinnerItemHolder = (SpinnerItemHolder) convertView.getTag();
        }
        HashMap<String, String> course = getItem(position);
        if (course != null) {
            spinnerItemHolder.bindData(course);
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @SuppressWarnings("WeakerAccess")
    static class SpinnerItemHolder {
        TextView spinnerItemView;

        public SpinnerItemHolder(View convertView) {
            spinnerItemView = convertView.findViewById(R.id.spinner_item);
        }

        void bindData(HashMap<String, String> itemProps) {
            String courseName = itemProps.get(FinanceLearningConstants.COURSE_NAME);
            if (courseName != null) {
                spinnerItemView.setText(courseName);
            }
        }
    }

}
