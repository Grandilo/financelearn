package com.grandilo.financelearn.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

/**
 * Created by Ugo
 */

public class VideoPlaybackActivity extends AppCompatActivity implements EasyVideoCallback {

    private EasyVideoPlayer easyVideoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        easyVideoPlayer = (EasyVideoPlayer) findViewById(R.id.player);
        String videoUrl = getIntent().getExtras().getString(FinanceLearningConstants.VIDEO_URL);
        String videoName = getIntent().getExtras().getString(FinanceLearningConstants.VIDEO_NAME);
        easyVideoPlayer.setCallback(this);
        if (videoUrl != null) {
            easyVideoPlayer.setSource(Uri.parse(videoUrl));
        }
        if (videoName != null) {
            getSupportActionBar().setTitle(Html.fromHtml(videoName));
        }
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
    protected void onPause() {
        super.onPause();
        easyVideoPlayer.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

}
