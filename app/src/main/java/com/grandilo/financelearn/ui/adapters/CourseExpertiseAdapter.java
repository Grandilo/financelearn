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

import java.util.List;

/**
 * @author Ugo
 */

public class CourseExpertiseAdapter extends ArrayAdapter<String> {

    private LayoutInflater layoutInflater;

    public CourseExpertiseAdapter(@NonNull Context context, List<String> experties) {
        super(context, 0, experties);
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
       String expertise = getItem(position);
        if (expertise != null) {
            spinnerItemHolder.bindData(expertise);
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

        void bindData(String experties) {
            spinnerItemView.setText(experties);
        }

    }


}
