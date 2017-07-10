package com.grandilo.financelearn.ui.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.UiUtils;

public class UpdatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText currentPasswordBox, newPasswordBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initViews();
    }

    private void initViews() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        currentPasswordBox = (EditText) findViewById(R.id.current_password_box);
        newPasswordBox = (EditText) findViewById(R.id.new_password_box);
        continueButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continue_button:
                if (TextUtils.isEmpty(currentPasswordBox.getText().toString().trim())) {
                    currentPasswordBox.setError("Please provide your current password");
                    return;
                }
                if (TextUtils.isEmpty(newPasswordBox.getText().toString().trim())) {
                    newPasswordBox.setError("Please provide your new password");
                    return;
                }
                FirebaseUser signedInUser = FirebaseAuth.getInstance().getCurrentUser();
                if (signedInUser != null) {
                    UiUtils.showProgressDialog(UpdatePasswordActivity.this, "Please wait");
                    signedInUser.updatePassword(newPasswordBox.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UiUtils.dismissProgressDialog();
                                    Toast.makeText(UpdatePasswordActivity.this, "Your password has being updated successfully!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UiUtils.dismissProgressDialog();
                                    Toast.makeText(UpdatePasswordActivity.this, "An error occurred while updating your password. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
                break;
        }
    }

}
