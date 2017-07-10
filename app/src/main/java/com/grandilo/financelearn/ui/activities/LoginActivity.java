package com.grandilo.financelearn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.UiUtils;

/**
 * @author Ugo
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView loginTypeView;
    private EditText idBox, passwordBox;
    private String loginType;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        initViews();
        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            loginType = intentExtras.getString(FinanceLearningConstants.LOGIN_TYPE);
            setUpLoginType();
        }
        initAuthStateListener();
    }

    private void initAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser loggedInUser = firebaseAuth.getCurrentUser();
                if (loggedInUser != null) {
                    UiUtils.dismissProgressDialog();
                    Intent intentExtras = new Intent();
                    intentExtras.putExtra(FinanceLearningConstants.LOGIN_TYPE, loginType);
                    setResult(RESULT_OK, intentExtras);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authStateListener != null) {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
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
                UiUtils.showProgressDialog(LoginActivity.this, "Please wait...");
                FirebaseAuth.getInstance().signInWithEmailAndPassword(idBox.getText()
                                .toString().trim() + FinanceLearningConstants.EMAIL_DOMAIN_SUFFIX,
                        passwordBox.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.dismissProgressDialog();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.dismissProgressDialog();
                                Toast.makeText(LoginActivity.this, "Sorry, " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                break;
        }
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
            case FinanceLearningConstants.LOGIN_TYPE_HR:
                loginTypeView.setText(getString(R.string.hr_login));
                idBox.setHint(getString(R.string.hr_id));
                break;
        }
    }

}
