package com.grandilo.financelearn.ui.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
    private Activity context;

    public LibrariesAdapter(Activity context, List<HashMap<String, String>> libraries) {
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
        libraryItemHolder.bindFile(context, position, stringStringHashMap);
    }

    @Override
    public int getItemCount() {
        return libraries != null ? libraries.size() : 0;
    }

    @SuppressWarnings("WeakerAccess")
    static class LibraryItemHolder extends RecyclerView.ViewHolder {

        private TextView fileNameView;
        private View divider;

        public LibraryItemHolder(View itemView) {
            super(itemView);
            fileNameView = itemView.findViewById(R.id.file_name);
            divider = itemView.findViewById(R.id.divider);
        }

        void bindFile(final Activity context, int position, final HashMap<String, String> stringStringHashMap) {
            if (position == 2) {
                divider.setVisibility(View.GONE);
            }
            final String fileName = stringStringHashMap.get(FinanceLearningConstants.FILE_NAME);
            fileNameView.setText(fileName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfForStoragePermission(context)) {
                            FinanceLearningConstants.lastDownloadableFileName = fileName;
                            FinanceLearningConstants.lastDownloadableFileMap = stringStringHashMap;
                            requestStoragePermissions(context);
                        } else {
                            tryDownloadOpenFile(fileName, context, stringStringHashMap);
                        }
                    } else {
                        tryDownloadOpenFile(fileName, context, stringStringHashMap);
                    }
                }
            });
        }

    }

    private static boolean checkSelfForStoragePermission(Activity activity) {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED);
    }

    private static void requestStoragePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, FinanceLearningConstants.STORAGE_PERMISSION_REQUEST_CODE);
    }

    public static void tryDownloadOpenFile(final String fileName, final Context context, final HashMap<String, String> stringStringHashMap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/FinanceLearn/Libraries/" + fileName + ".pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Download File");
            builder.setMessage("Click continue to download this pdf so you can view it. Download is once.");
            builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    UiUtils.showToast("Please wait. Downloading pdf file. You will be able to view the document when download is complete");
                    initDownloadService(context, stringStringHashMap.get(fileName), fileName);
                }
            }).setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private static void initDownloadService(Context context, String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading " + fileName);
        request.setTitle(fileName);
        // in order for this if to run, you must use the android 3.2 to compile your app
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/FinanceLearn/Libraries/" + fileName + ".pdf");
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long refId = manager.enqueue(request);
        FinanceLearningConstants.downloadRefIds.put(refId, fileName);
    }

}
