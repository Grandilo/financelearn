package com.grandilo.financelearn.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class EmployeeHomeScreen extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);
        initViews();
        populateSignedInUserProps();
    }

    private void finishLogOut() {
        AppPreferences.saveLoggedInType(EmployeeHomeScreen.this, null);
        Intent splashScreenIntent = new Intent(EmployeeHomeScreen.this, SplashActivity.class);
        startActivity(splashScreenIntent);
    }

    private void populateSignedInUserProps() {
        JSONObject signedInUserProps = AppPreferences.getSignedInUser(this);
        if (signedInUserProps != null) {
            String lastName = signedInUserProps.optString(FinanceLearningConstants.LAST_NAME);
            String surname = signedInUserProps.optString(FinanceLearningConstants.SURNAME);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Html.fromHtml("Welcome, <b>" + surname + " " + lastName + "</b>"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employee, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        View myCoursesView = findViewById(R.id.my_courses_view);
        myCoursesView.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.update_password) {
            initPasswordResetDialog();
            return true;
        } else if (item.getItemId() == R.id.sign_out) {
            AppPreferences.saveLoggedInUser(EmployeeHomeScreen.this, null);
            finishLogOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_courses_view:
                Intent preTestIntent = new Intent(EmployeeHomeScreen.this, PreAvailableCoursesActivity.class);
                startActivity(preTestIntent);
                break;
        }
    }

    private void initPasswordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Your Password");
        builder.setMessage("Do you wish to update your password?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mUpdatePasswordIntent = new Intent(EmployeeHomeScreen.this, UpdatePasswordActivity.class);
                startActivity(mUpdatePasswordIntent);
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

}
