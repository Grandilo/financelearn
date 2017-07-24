package com.grandilo.financelearn.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.UploadTask;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.CourseExpertiseAdapter;
import com.grandilo.financelearn.ui.adapters.CourseSelectionSpinnerAdapter;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.FirebaseUtils;
import com.grandilo.financelearn.utils.MediaUploadUtils;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class UploadCourseVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner courseCategorySpinner, expertiseLevelSpinner;

    private ChildEventListener coursesEventListener;
    private DatabaseReference courseReference;

    private List<HashMap<String, String>> courses = new ArrayList<>();
    private CourseSelectionSpinnerAdapter courseSelectionSpinnerAdapter;

    private List<String> expertise = new ArrayList<>();

    private HashMap<String, Object> courseReps = new HashMap<>();
    private HashMap<String, Object> pickedVideoMap = new HashMap<>();

    private ImageView videoPreview;
    private FrameLayout videoPreviewContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_course_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        courseReference = FirebaseUtils.getCourses();
        initViews();
        initCoursesSpinnerAdapter();
        initExpertiseAdapter();
        fetchCourses();
    }

    private void handleIntent(Intent intent) {
        boolean cancelUpload = intent.getBooleanExtra(FinanceLearningConstants.CANCEL_UPLOAD, false);
        if (cancelUpload) {
            int operationId = intent.getIntExtra(FinanceLearningConstants.OPERATION_ID, -1);
            if (operationId != -1) {
                Log.d("UploadVideo", "OperationId = " + operationId);
                UploadTask uploadTask = FinanceLearningConstants.taskQueue.get(operationId);
                if (uploadTask != null) {
                    Log.d("UploadVideo", "Task found");
                    //Cancel the upload task as well
                    uploadTask.cancel();
                    MediaUploadUtils.getNotificationManager().cancel(operationId);
                    FinanceLearningConstants.taskQueue.remove(operationId);
                    Log.d("UploadVideo", "Cancelled found");
                } else {
                    Log.d("UploadVideo", "Task not found");
                }
            } else {
                Log.d("UploadVideo", "Operation Id is negative");
            }
        } else {
            Log.d("UploadVideo", "Operation not to be cancelled");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void initCoursesSpinnerAdapter() {
        courseSelectionSpinnerAdapter = new CourseSelectionSpinnerAdapter(this, courses);
        courseCategorySpinner.setAdapter(courseSelectionSpinnerAdapter);
    }

    private void initExpertiseAdapter() {
        expertise.add(FinanceLearningConstants.BASIC);
        expertise.add(FinanceLearningConstants.INTERMEDIATE);
        expertise.add(FinanceLearningConstants.EXPERT);

        CourseExpertiseAdapter courseExpertiseAdapter = new CourseExpertiseAdapter(this, expertise);
        expertiseLevelSpinner.setAdapter(courseExpertiseAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (courseReference != null && coursesEventListener != null) {
            courseReference.removeEventListener(coursesEventListener);
        }
    }

    private void initViews() {
        Button pickVideoButton = (Button) findViewById(R.id.pick_video);
        Button uploadVideoButton = (Button) findViewById(R.id.upload_video);

        courseCategorySpinner = (Spinner) findViewById(R.id.course_category_spinner);
        expertiseLevelSpinner = (Spinner) findViewById(R.id.exper_level_spinner);
        videoPreview = (ImageView) findViewById(R.id.video_preview);

        videoPreviewContainer = (FrameLayout) findViewById(R.id.video_preview_container);

        pickVideoButton.setOnClickListener(this);
        uploadVideoButton.setOnClickListener(this);

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
                courseSelectionSpinnerAdapter.notifyDataSetChanged();

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

    public static void startVideoPicker(Activity activity) {
        new VideoPicker.Builder(activity)
                .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                .directory(VideoPicker.Directory.DEFAULT)
                .build();
    }

    private void prepareUpload() {
        if (pickedVideoMap.isEmpty()) {
            Toast.makeText(this, "Please pick a video to upload", Toast.LENGTH_LONG).show();
            return;
        }
        if (expertiseLevelSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a course expertise level", Toast.LENGTH_LONG).show();
            return;
        }
        if (courseCategorySpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a course category", Toast.LENGTH_LONG).show();
        }
        courseReps.put(FinanceLearningConstants.EXPERTISE_LEVEL, expertiseLevelSpinner.getSelectedItem());
        courseReps.put(FinanceLearningConstants.COURSE_DETAILS, courseCategorySpinner.getSelectedItem());
        Toast.makeText(this, "Upload started. Check your notification tray", Toast.LENGTH_LONG).show();
        MediaUploadUtils.uploadVideoAndPushCourse(pickedVideoMap, courseReps);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.upload_video) {
            //Upload Video
            prepareUpload();
        } else if (view.getId() == R.id.pick_video) {
            startVideoPicker(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            final String pickedVideoPath = data.getStringExtra(VideoPicker.EXTRA_VIDEO_PATH);
            if (!TextUtils.isEmpty(pickedVideoPath)) {

                File filePath = new File(pickedVideoPath);

                Log.d("PickedFile",filePath.getName());

                HashMap<String, Object> pickedVideoProps = new HashMap<>();
                pickedVideoProps.put(FinanceLearningConstants.MEDIA_LOCAL_URL, pickedVideoPath);

                pickedVideoMap.put(FinanceLearningConstants.COURSE_FILES, pickedVideoProps);

                Bitmap videoThumbnail = MediaUploadUtils.generateVideoThumbnail(pickedVideoPath);

                if (videoThumbnail != null) {
                    videoPreviewContainer.setVisibility(View.VISIBLE);
                    videoPreview.setImageBitmap(videoThumbnail);
                } else {
                    videoPreviewContainer.setVisibility(View.GONE);
                }
                videoPreviewContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent videoViewIntent = new Intent(Intent.ACTION_VIEW);
                        videoViewIntent.setDataAndType(Uri.parse(pickedVideoPath), "video/*");
                        if (getPackageManager().queryIntentActivities(videoViewIntent, 0).size() > 0) {
                            startActivity(videoViewIntent);
                        } else {
                            Toast.makeText(UploadCourseVideoActivity.this, "Sorry, we couldn't detect any video player in your device.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}
