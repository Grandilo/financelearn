package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

/**
 * @author Ugo
 */

public class StaffProfileManagementScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView staffCoursesRecyclerView;
    private String staffName, staffId;
    private String sourceActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_profile_management);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        staffCoursesRecyclerView = (RecyclerView) findViewById(R.id.staff_recycler_view);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        offloadIntent();
    }

    private void offloadIntent() {
        Bundle intentExtras = getIntent().getExtras();
        staffName = intentExtras.getString(FinanceLearningConstants.EMPLOYEE_NAME);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(staffName);
        }
        staffId = intentExtras.getString(FinanceLearningConstants.EMPLOYEE_ID);
        sourceActivity = intentExtras.getString(FinanceLearningConstants.SOURCE_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        if (sourceActivity == null) {
            Intent staffListManagementIntent = new Intent(this, ManagerHomeScreen.class);
            startActivity(staffListManagementIntent);
        } else {
            super.onBackPressed();
        }
    }

}
