package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.VideosAdapter;
import com.grandilo.financelearn.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class VideosActivity extends AppCompatActivity {

    private List<HashMap<String, Object>> videos = new ArrayList<>();
    private VideosAdapter videosAdapter;

    private ChildEventListener videosListener;
    private DatabaseReference videosReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView videosRecyclerView = (RecyclerView) findViewById(R.id.videos_recycler_view);

        videosAdapter = new VideosAdapter(this, videos);
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        videosRecyclerView.setAdapter(videosAdapter);

        videosReference = FirebaseUtils.getCourseVideosReference();

        fetchVideos();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchVideos() {

        videosListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null) {

                    GenericTypeIndicator<HashMap<String, Object>> hashMapGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};

                    HashMap<String, Object> stringObjectHashMap = dataSnapshot.getValue(hashMapGenericTypeIndicator);

                    if (stringObjectHashMap != null) {
                        if (!videos.contains(stringObjectHashMap)){
                            videos.add(stringObjectHashMap);
                            videosAdapter.notifyDataSetChanged();
                        }
                    }

                }

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

        videosReference.addChildEventListener(videosListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videosListener!=null && videosReference!=null){
            videosReference.removeEventListener(videosListener);
        }
    }

}
