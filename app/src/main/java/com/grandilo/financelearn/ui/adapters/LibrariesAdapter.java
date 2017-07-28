package com.grandilo.financelearn.ui.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.utils.FinanceLearningConstants;
import com.grandilo.financelearn.utils.UiUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class LibrariesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HashMap<String, String>> libraries;
    private LayoutInflater layoutInflater;
    private Context context;

    public LibrariesAdapter(Context context, List<HashMap<String, String>> libraries) {
        this.context = context;
        this.libraries = libraries;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.library_recycler_view_item, parent, false);
        return new LibraryItemHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LibraryItemHolder libraryItemHolder = (LibraryItemHolder) holder;

        HashMap<String, String> stringStringHashMap = libraries.get(position);

        libraryItemHolder.bindFile(context, stringStringHashMap);
    }

    @Override
    public int getItemCount() {
        return libraries != null ? libraries.size() : 0;
    }

    @SuppressWarnings("WeakerAccess")
    static class LibraryItemHolder extends RecyclerView.ViewHolder {

        private TextView fileNameView;

        public LibraryItemHolder(View itemView) {
            super(itemView);
            fileNameView = itemView.findViewById(R.id.file_name);
        }

        void bindFile(final Context context, final HashMap<String, String> stringStringHashMap) {
            final String fileName = stringStringHashMap.get(FinanceLearningConstants.FILE_NAME);
            fileNameView.setText(fileName);
            fileNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(Environment.DIRECTORY_DOWNLOADS+"/"+ fileName);
                    if (file.exists()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                    } else {
                        UiUtils.showToast("Downloading pdf file. You will be able to view the document when download is complete");
                        initDownloadService(context, stringStringHashMap.get(fileName), fileName);
                    }
                }
            });
        }

        private void initDownloadService(Context context, String url, String fileName) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading " + fileName);
            request.setTitle(fileName);
            // in order for this if to run, you must use the android 3.2 to compile your app
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }

    }

}
