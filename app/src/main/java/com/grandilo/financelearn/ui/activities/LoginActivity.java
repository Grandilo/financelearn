package com.grandilo.financelearn.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.UiUtils;

import java.util.HashMap;

/**
 * @author Ugo
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView loginTypeView;
    private EditText idBox, passwordBox;
    private String loginType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            loginType = intentExtras.getString(FinanceLearningConstants.LOGIN_TYPE);
            setUpLoginType();
        }
    }

    private void initViews() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        idBox = (EditText) findViewById(R.id.id_box);
        passwordBox = (EditText) findViewById(R.id.password_box);
        loginTypeView = (TextView) findViewById(R.id.login_type_view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continue_button:
                if (TextUtils.isEmpty(idBox.getText().toString().trim())) {
                    idBox.setError("Please your id");
                    return;
                }
                if (TextUtils.isEmpty(passwordBox.getText().toString().trim())) {
                    passwordBox.setError("Please your password");
                    return;
                }
                dismissKeyboard(idBox);
                UiUtils.showProgressDialog(LoginActivity.this, "Please wait...");
                attemptStaffAuthentication(idBox.getText().toString().trim(), passwordBox.getText().toString().trim());
                break;
        }
    }

    private void attemptStaffAuthentication(final String staffId, final String staffPassword) {

        FirebaseUtils.getStaffReference().orderByChild("staff_id").equalTo(staffId.trim()).addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressWarnings("unchecked")
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                UiUtils.dismissProgressDialog();

                if (dataSnapshot != null && dataSnapshot.getChildrenCount() > 0) {

                    Log.d("Firebase", "Children Count = " + dataSnapshot.getChildrenCount() + " and data = " + dataSnapshot.toString());

                    GenericTypeIndicator<HashMap<String, HashMap<String, Object>>> hashMapGenericTypeIndicator =
                            new GenericTypeIndicator<HashMap<String, HashMap<String, Object>>>() {
                            };

                    HashMap<String, HashMap<String, Object>> mapProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                    Log.d("Firebase", "DataSnapShot = " + dataSnapshot.getKey());

                    if (mapProps != null) {
                        for (String key : mapProps.keySet()) {
                            HashMap<String, Object> propertiesMap = mapProps.get(key);
                            if (propertiesMap != null) {
                                Log.d("Firebase", "User Props = " + propertiesMap.toString());
                                //A user exists
                                String userPassword = (String) propertiesMap.get(FinanceLearningConstants.PASSWORD);
                                Log.d("Firebase", "User Password = " + userPassword);
                                if (userPassword != null && userPassword.trim().equals(staffPassword.trim())) {
                                    AppPreferences.saveLoggedInType(LoginActivity.this, loginType);
                                    AppPreferences.saveLoggedInUser(LoginActivity.this, propertiesMap);
                                    //This user has an account
                                    Intent intent = new Intent(LoginActivity.this, AuthenticationActivity.class);
                                    intent.putExtra(FinanceLearningConstants.LOGIN_TYPE, loginType);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    //Invalid password or no account created yet
                                    Toast.makeText(LoginActivity.this, "Sorry, " + "Invalid staffId and password combinations", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                //No such user was found.
                                Toast.makeText(LoginActivity.this, "Sorry, " + "No such staff was found", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                } else {
                    //Invalid password or no account created yet
                    Toast.makeText(LoginActivity.this, "Sorry, " + "No result found", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                UiUtils.dismissProgressDialog();
                Toast.makeText(LoginActivity.this, "Sorry, " + "Your last operation was cancelled. Please try again", Toast.LENGTH_LONG).show();
            }

        });

    }

    public void setUpLoginType() {
        switch (loginType) {
            case FinanceLearningConstants.LOGIN_TYPE_EMPLOYEE:
                loginTypeView.setText(getString(R.string.employee_login));
                idBox.setHint(getString(R.string.your_employee_id));
                break;
            case FinanceLearningConstants.LOGIN_TYPE_MANAGER:
                loginTypeView.setText(getString(R.string.manager_login));
                idBox.setHint(getString(R.string.you_id));
                break;
        }
    }

    public void dismissKeyboard(View trigger) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(trigger.getWindowToken(), 0);
    }

}
