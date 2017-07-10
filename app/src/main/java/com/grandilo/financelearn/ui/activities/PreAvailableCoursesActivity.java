package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.PreTestCoursesAdapter;
import com.grandilo.financelearn.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

@SuppressWarnings("deprecation")
public class PreAvailableCoursesActivity extends AppCompatActivity implements View.OnClickListener {

    View compulsoryCoursesNotSelectedYet;
    TextView welcomeHeaderView;

    private RecyclerView coursesRecyclerView;

    private List<HashMap<String, String>> courses = new ArrayList<>();

    private ChildEventListener coursesEventListener;
    private DatabaseReference courseReference;

    private PreTestCoursesAdapter preTestCoursesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_available_courses);
        TextView managerCoursesUnselectedMessageView = (TextView) findViewById(R.id.courses_unselected_message);
        managerCoursesUnselectedMessageView.setText(Html.fromHtml("Your manager hasn't selected <b>2 courses</b> yet"));
        initViews();
        initCoursesAdapter();
        courseReference = FirebaseUtils.getCourses();
        fetchCourses();
    }

    private void initViews() {
        compulsoryCoursesNotSelectedYet = findViewById(R.id.compulsory_courses_not_selected_yet);
        TextView sendNotificationTextView = (TextView) findViewById(R.id.send_notification);
        sendNotificationTextView.setOnClickListener(this);
        TextView cancelActionTextView = (TextView) findViewById(R.id.cancel_action);
        cancelActionTextView.setOnClickListener(this);
        welcomeHeaderView = (TextView) findViewById(R.id.welcome_header);
        welcomeHeaderView.setText(Html.fromHtml("<b>6</b> courses Available, \nchoose <b>2</b>"));
        coursesRecyclerView = (RecyclerView) findViewById(R.id.available_courses_recycler_view);
    }

    private void initCoursesAdapter() {
        preTestCoursesAdapter = new PreTestCoursesAdapter(this, courses);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        coursesRecyclerView.setLayoutManager(verticalLayoutManager);
        coursesRecyclerView.setAdapter(preTestCoursesAdapter);
    }

    private void fetchCourses() {

        coursesEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<HashMap<String, String>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() {
                };

                HashMap<String, String> courseProps = dataSnapshot.getValue(hashMapGenericTypeIndicator);
                courses.add(courseProps);
                preTestCoursesAdapter.notifyDataSetChanged();
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

    }

}
