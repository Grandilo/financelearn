package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.StaffProfileManagementScreen;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class StaffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HashMap<String, Object>> staffList;
    private LayoutInflater layoutInflater;
    private Context context;

    public StaffAdapter(Context context, List<HashMap<String, Object>> staffList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.staffList = staffList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.staff_item, parent, false);
        return new StaffItemHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StaffItemHolder staffItemHolder = (StaffItemHolder) holder;
        HashMap<String, Object> staffDetails = staffList.get(position);
        staffItemHolder.bindStaffDetails(context, staffDetails);
    }

    @Override
    public int getItemCount() {
        return staffList != null ? staffList.size() : 0;
    }

    @SuppressWarnings({"WeakerAccess", "RedundantCast"})
    static class StaffItemHolder extends RecyclerView.ViewHolder {

        TextView staffNameView;

        public StaffItemHolder(View itemView) {
            super(itemView);
            staffNameView = (TextView) itemView.findViewById(R.id.staff_name);
        }

        void bindStaffDetails(final Context context, final HashMap<String, Object> staffDetails) {

            String employeeName = (String) staffDetails.get(FinanceLearningConstants.EMPLOYEE_NAME);

            staffNameView.setText(employeeName);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent staffProfileIntent = new Intent(context, StaffProfileManagementScreen.class);
                    staffProfileIntent.putExtra(FinanceLearningConstants.EMPLOYEE_ID, (String) staffDetails.get(FinanceLearningConstants.EMPLOYEE_ID));
                    staffProfileIntent.putExtra(FinanceLearningConstants.EMPLOYEE_NAME, (String) staffDetails.get(FinanceLearningConstants.EMPLOYEE_NAME));
                    staffProfileIntent.putExtra(FinanceLearningConstants.SOURCE_ACTIVITY, context.getClass().getSimpleName());
                    context.startActivity(staffProfileIntent);
                }

            });

        }

    }

}
