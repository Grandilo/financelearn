package com.grandilo.financelearn.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class VideoThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HashMap<String, Object>> videoProps;
    private LayoutInflater layoutInflater;
    private Context context;

    public VideoThumbnailsAdapter(Context context, List<HashMap<String, Object>> videoProps) {
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

        void bindVideo(Context context, HashMap<String, Object> videoProps) {

            HashMap<String, Object> courseDetails = (HashMap<String, Object>) videoProps.get(FinanceLearningConstants.COURSE_DETAILS);

            String expertLevel = (String) videoProps.get(FinanceLearningConstants.EXPERTISE_LEVEL);

            if (courseDetails != null && expertLevel != null) {
                String courseName = (String) courseDetails.get(FinanceLearningConstants.COURSE_NAME);
                if (courseName != null) {
                    courseTitleView.setText(expertLevel + " video on " + courseName);
                }
            }

            HashMap<String, Object> thumbProps = (HashMap<String, Object>) videoProps.get(FinanceLearningConstants.COURSE_VIDEO);
            if (thumbProps != null) {
                String videoThumbnail = (String) thumbProps.get(FinanceLearningConstants.THUMB_NAIL);
                Glide.with(context).load(videoThumbnail).diskCacheStrategy(DiskCacheStrategy.ALL).into(thumbnailPreviewImageView);
            }

        }

    }

}
