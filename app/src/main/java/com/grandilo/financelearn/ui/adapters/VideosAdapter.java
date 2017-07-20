package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.activities.VideoPlaybackActivity;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */
public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HashMap<String, Object>> videoProps;
    private LayoutInflater layoutInflater;
    private Context context;

    public VideosAdapter(Context context, List<HashMap<String, Object>> videoProps) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.videoProps = videoProps;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.thumbnail_preview_recycler_item, parent, false);
        return new VideoThumbnailItemHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoThumbnailItemHolder videoThumbnailItemHolder = (VideoThumbnailItemHolder) holder;
        HashMap<String, Object> videoProp = videoProps.get(position);
        if (videoProp != null) {
            videoThumbnailItemHolder.bindVideo(context, videoProp);
        }
    }

    @Override
    public int getItemCount() {
        return videoProps != null ? videoProps.size() : 0;
    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    static class VideoThumbnailItemHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailPreviewImageView;
        private TextView courseTitleView;

        public VideoThumbnailItemHolder(View itemView) {
            super(itemView);
            thumbnailPreviewImageView = itemView.findViewById(R.id.video_thumbnail_view);
            courseTitleView = itemView.findViewById(R.id.course_title);
        }

        void bindVideo(final Context context, HashMap<String, Object> videoProps) {

            HashMap<String, Object> courseDetails = (HashMap<String, Object>) videoProps.get(FinanceLearningConstants.COURSE_DETAILS);

            final String expertLevel = (String) videoProps.get(FinanceLearningConstants.EXPERTISE_LEVEL);

            String courseName = null;

            if (courseDetails != null && expertLevel != null) {
                courseName = (String) courseDetails.get(FinanceLearningConstants.COURSE_NAME);
                if (courseName != null) {
                    courseTitleView.setText(Html.fromHtml(expertLevel + " video on <b>" + courseName + "</b>"));
                }
            }

            HashMap<String, Object> thumbProps = (HashMap<String, Object>) videoProps.get(FinanceLearningConstants.COURSE_VIDEO);
            if (thumbProps != null) {
                String videoThumbnail = (String) thumbProps.get(FinanceLearningConstants.THUMB_NAIL);
                final String videoRemoteUrl = (String) thumbProps.get(FinanceLearningConstants.REMOTE_URL);
                Log.d("Thumbs", videoThumbnail);
                Glide.with(context).load(videoThumbnail).diskCacheStrategy(DiskCacheStrategy.ALL).into(thumbnailPreviewImageView);
                final String finalCourseName = courseName;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent videoPlaybackIntent  = new Intent(context, VideoPlaybackActivity.class);
                        videoPlaybackIntent.putExtra(FinanceLearningConstants.VIDEO_URL,videoRemoteUrl);
                        videoPlaybackIntent.putExtra(FinanceLearningConstants.VIDEO_NAME,expertLevel + " video on <b>" + finalCourseName + "</b>");
                        context.startActivity(videoPlaybackIntent);
                    }
                });
            } else {
                Log.d("Thumbs", "ThumbProps are kindda null");
            }

        }

    }

}
