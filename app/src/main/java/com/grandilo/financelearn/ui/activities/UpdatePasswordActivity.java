package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.UiUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class UpdatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText currentPasswordBox, newPasswordBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
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
                JSONObject signedInUser = AppPreferences.getSignedInUser(UpdatePasswordActivity.this);
                if (signedInUser != null) {
                    String userId = signedInUser.optString("staff_id");
                    UiUtils.showProgressDialog(UpdatePasswordActivity.this, "Please wait");
                    HashMap<String, Object> updatableProps = new HashMap<>();
                    updatableProps.put("password", newPasswordBox.getText().toString().trim());

                    FirebaseUtils.getStaffReference().child(userId).updateChildren(updatableProps,
                            new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (databaseError != null) {
                                        UiUtils.dismissProgressDialog();
                                        Toast.makeText(UpdatePasswordActivity.this, "Your password has being updated successfully!", Toast.LENGTH_LONG).show();
                                        AppPreferences.saveNotFirstLogIn();
                                        finish();
                                    } else {
                                        UiUtils.dismissProgressDialog();
                                        Toast.makeText(UpdatePasswordActivity.this, "An error occurred while updating your password. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
                break;

        }

    }

}
