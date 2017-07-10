package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        initViews();
    }

    private void initViews() {
        Button staffLoginButton = (Button) findViewById(R.id.employee_login);
        Button managerLoginButton = (Button) findViewById(R.id.manager_login);
        Button hrLoginButton = (Button) findViewById(R.id.hr_login);

        staffLoginButton.setOnClickListener(this);
        managerLoginButton.setOnClickListener(this);
        hrLoginButton.setOnClickListener(this);
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
            case R.id.hr_login:
                launchLoginActivity(FinanceLearningConstants.LOGIN_TYPE_HR);
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
            FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
            if (loggedInUser != null) {
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
                    default:
                        Intent hrHomeScreenIntent = new Intent(AuthenticationActivity.this, HrHomeScreen.class);
                        startActivity(hrHomeScreenIntent);
                        break;
                }
                finish();
            }
        }
    }

}
