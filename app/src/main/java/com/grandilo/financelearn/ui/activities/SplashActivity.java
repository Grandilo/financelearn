package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

/**
 * The splash screen launches the app and check if a user has logged into the app.
 *
 * @author Ugo
 ***/
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAuthenticationStatus();
    }

    /**
     * Method checks if a Firebase User has Logged
     ***/
    private void checkAuthenticationStatus() {
        boolean isUserLoggedIn = AppPreferences.isUserLoggedIn(this);
        if (isUserLoggedIn) {
            //A user has logged into the app, let's move on
            String loggedType = AppPreferences.getLoggedInType(this);
            if (loggedType != null) {
                if (loggedType.equals(FinanceLearningConstants.LOGIN_TYPE_EMPLOYEE)) {
                    Intent mEmployeeHomeScreenIntent = new Intent(SplashActivity.this, EmployeeHomeScreen.class);
                    startActivity(mEmployeeHomeScreenIntent);
                }else if (loggedType.equals(FinanceLearningConstants.LOGIN_TYPE_MANAGER)){
                    Intent mManagerIntent = new Intent(SplashActivity.this, ManagerHomeScreen.class);
                    startActivity(mManagerIntent);
                }
                finish();
            }
        } else {
            //No has has logged into the app,let's sign him/her in automatically
            Intent authIntent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            startActivity(authIntent);
        }
        finish();
    }

}
