package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

/**
 * This activity authenticates a user into the app
 *
 * @author Ugo
 */

public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener {

    private int LOGIN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_authentication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        Button staffLoginButton = (Button) findViewById(R.id.employee_login);
        Button managerLoginButton = (Button) findViewById(R.id.manager_login);
        Button guestLogingButton = (Button)findViewById(R.id.guest_login);

        staffLoginButton.setOnClickListener(this);
        managerLoginButton.setOnClickListener(this);
        guestLogingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.employee_login:
                launchLoginActivity(FinanceLearningConstants.LOGIN_TYPE_EMPLOYEE);
                break;
            case R.id.manager_login:
                launchLoginActivity(FinanceLearningConstants.LOGIN_TYPE_MANAGER);
                break;
            case R.id.guest_login:
                launchLoginActivity(FinanceLearningConstants.LOGIN_TYPE_GUEST);
                break;
        }
    }

    private void launchLoginActivity(String loginType) {
        Intent loginIntent = new Intent(AuthenticationActivity.this, LoginActivity.class);
        loginIntent.putExtra(FinanceLearningConstants.LOGIN_TYPE, loginType);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            String logInType = data.getStringExtra(FinanceLearningConstants.LOGIN_TYPE);
            boolean isUserLopgged = AppPreferences.isUserLoggedIn(AuthenticationActivity.this);
            if (isUserLopgged) {
                //A user has authenticated successfully
                AppPreferences.saveLoggedInType(AuthenticationActivity.this, logInType);
                switch (logInType) {
                    case FinanceLearningConstants.LOGIN_TYPE_EMPLOYEE:
                        Intent employHomeScreenIntent = new Intent(AuthenticationActivity.this, EmployeeHomeScreen.class);
                        startActivity(employHomeScreenIntent);
                        break;
                    case FinanceLearningConstants.LOGIN_TYPE_MANAGER:
                        Intent managerHomeScreenIntent = new Intent(AuthenticationActivity.this, ManagerHomeScreen.class);
                        startActivity(managerHomeScreenIntent);
                        break;
                }
                finish();
            }
        }
    }

}
