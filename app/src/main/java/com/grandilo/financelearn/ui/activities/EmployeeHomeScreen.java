package com.grandilo.financelearn.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class EmployeeHomeScreen extends AppCompatActivity implements View.OnClickListener {

    private TextView welcomeView;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home_screen);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        initViews();
        loadEmployeeScreen();
    }

    private void initViews() {
        welcomeView = (TextView) findViewById(R.id.welcome_header);
        View myCoursesView = findViewById(R.id.my_courses_view);
        myCoursesView.setOnClickListener(this);
        ImageView settingsView = (ImageView) findViewById(R.id.settings);
        settingsView.setOnClickListener(this);
    }

    private void loadEmployeeScreen() {
        FirebaseUtils.getStaffReference().child("employee_email")
                .equalTo(firebaseUser.getEmail())
                .limitToLast(1).
                addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        GenericTypeIndicator<HashMap<String, String>> hashMapGenericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String, String>>() {
                                };

                        HashMap<String, String> objectMap = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                        if (objectMap != null) {
                            String lastName = objectMap.get(FinanceLearningConstants.LAST_NAME);
                            String surname = objectMap.get(FinanceLearningConstants.SURNAME);
                            welcomeView.setText(Html.fromHtml("Welcome, <b>" + surname + " " + lastName + "</b>"));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_courses_view:
                Intent preTestIntent = new Intent(EmployeeHomeScreen.this, PreAvailableCoursesActivity.class);
                startActivity(preTestIntent);
                break;
            case R.id.settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Update Your Password");
                builder.setMessage("Do you wish to update your password?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent mUpdatePasswordIntent = new Intent(EmployeeHomeScreen.this,UpdatePasswordActivity.class);
                        startActivity(mUpdatePasswordIntent);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
    }

}
