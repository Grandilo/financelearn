package com.grandilo.financelearn.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.PreTestCourseSelectionAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings("deprecation")
@SuppressLint("StaticFieldLeak")

public class PreTestCourseSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView nextButton;
    private RecyclerView coursesRecyclerView;
    private List<HashMap<String, String>> courses = new ArrayList<>();
    private ChildEventListener coursesEventListener;
    private DatabaseReference courseReference;
    private static PreTestCourseSelectionAdapter preTestCourseSelectionAdapter;
    private JSONObject signedInUser;

    private static LinearLayout bottomBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_test_courses);

        TextView managerCoursesUnselectedMessageView = (TextView) findViewById(R.id.courses_unselected_message);
        managerCoursesUnselectedMessageView.setText(Html.fromHtml("Your manager hasn't selected <b>2 courses</b> yet"));

        nextButton = (TextView) findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        TextView sendNotificationTextView = (TextView) findViewById(R.id.send_notification);
        sendNotificationTextView.setOnClickListener(this);

        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        signedInUser = AppPreferences.getSignedInUser(this);

        if (signedInUser != null) {
            try {
                JSONArray assignedCourses = new JSONArray(signedInUser.getString(FinanceLearningConstants.COURSES_ASSIGNED));
                if (assignedCourses.length() > 0) {
                    managerCoursesUnselectedMessageView.setVisibility(View.GONE);
                    sendNotificationTextView.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.GONE);
                } else {
                    bottomBar.setVisibility(View.VISIBLE);
                    managerCoursesUnselectedMessageView.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                nextButton.setVisibility(View.GONE);
            }
        }
        initViews();
        initCoursesAdapter();
        courseReference = FirebaseUtils.getCourses();
        fetchCourses();

        clearAllNotificationsForCurrentStaff();
    }

    public static void reviewSelections() {
        int selectedCoursesCount = preTestCourseSelectionAdapter.getCoursesToTest().size();
        if (selectedCoursesCount == 4) {
            bottomBar.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        } else {
            bottomBar.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        TextView welcomeHeaderView = (TextView) findViewById(R.id.welcome_header);
        welcomeHeaderView.setText(Html.fromHtml("<b>6</b> courses Available, \nchoose <b>2</b>"));
        coursesRecyclerView = (RecyclerView) findViewById(R.id.available_courses_recycler_view);
    }

    private void initCoursesAdapter() {
        preTestCourseSelectionAdapter = new PreTestCourseSelectionAdapter(this, courses);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        coursesRecyclerView.setLayoutManager(verticalLayoutManager);
        coursesRecyclerView.setAdapter(preTestCourseSelectionAdapter);
    }

    private void fetchCourses() {

        coursesEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<HashMap<String, String>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {
                };

                HashMap<String, String> courseProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                if (courseProps != null) {
                    courseProps.put(FinanceLearningConstants.COURSE_ID, dataSnapshot.getKey());
                }
                courses.add(courseProps);
                preTestCourseSelectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        courseReference.addChildEventListener(coursesEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (courseReference != null && coursesEventListener != null) {
            courseReference.removeEventListener(coursesEventListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_notification:
                UiUtils.showProgressDialog(this, "Sending your manager a notification.");
                HashMap<String, Object> pushNotifProps = new HashMap<>();
                pushNotifProps.put(FinanceLearningConstants.EMPLOYEE_ID, signedInUser.optString("staff_id"));
                String lastName = signedInUser.optString(FinanceLearningConstants.LAST_NAME);
                String surname = signedInUser.optString(FinanceLearningConstants.SURNAME);
                String fullName = surname + " " + lastName;
                pushNotifProps.put(FinanceLearningConstants.EMPLOYEE_NAME, fullName);
                pushNotifProps.put(FinanceLearningConstants.NOTIFICATION_CATEGORY, FinanceLearningConstants.CATEGORY_ASSIGN_ME_COURSES);
                pushNotifProps.put(FinanceLearningConstants.NOTIFICATIONS_TARGET, signedInUser.optString("staff_manager_id"));
                FirebaseUtils.getNotificationsNode().push().setValue(pushNotifProps, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        UiUtils.dismissProgressDialog();
                        if (databaseError == null) {
                            Toast.makeText(PreTestCourseSelectionActivity.this, "A notification was successfully sent to your manager.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PreTestCourseSelectionActivity.this, "Sorry, an error occurred while sending your notification. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.next_button:
                //Prepare questions here
                ArrayList<String> selectedCourseIds = preTestCourseSelectionAdapter.getCoursesToTest();
                Intent preTestIntent = new Intent(PreTestCourseSelectionActivity.this, Pretest.class);
                preTestIntent.putStringArrayListExtra(FinanceLearningConstants.SELECTED_PRE_TEST_COURSES, selectedCourseIds);
                startActivity(preTestIntent);
                break;
        }

    }

    private void clearAllNotificationsForCurrentStaff() {
        FirebaseUtils.getNotificationsNode()
                .orderByChild(FinanceLearningConstants.NOTIFICATIONS_TARGET)
                .equalTo(signedInUser.optString("staff_id"))
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FirebaseUtils.getNotificationsNode().child(snapshot.getKey()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

    }

}
