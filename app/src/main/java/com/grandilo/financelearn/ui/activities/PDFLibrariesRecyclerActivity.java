package com.grandilo.financelearn.ui.activities;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.LibrariesAdapter;
import com.grandilo.financelearn.utils.AppPreferences;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class PDFLibrariesRecyclerActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private List<HashMap<String, String>> pdfLibraries = new ArrayList<>();
    private RecyclerView librariesRecyclerView;

    BroadcastReceiver downloadsCompleteReceiver = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            // get the refid from the download manager
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // show a notification
            Log.d("INSIDE", "" + referenceId);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(PDFLibrariesRecyclerActivity.this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("FinanceLearn PDF Libraries")
                            .setContentText(AppPreferences.getDownloadReference(referenceId) + " Download Completed");

            PendingIntent pendingIntent = null;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/FinanceLearn/Libraries/" + AppPreferences.getDownloadReference(referenceId) + ".pdf");
            if (file.exists()) {
                Intent launcherIntent = new Intent(Intent.ACTION_VIEW);
                launcherIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
                launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                pendingIntent = PendingIntent.getActivity(PDFLibrariesRecyclerActivity.this, 0, launcherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            if (pendingIntent != null) {
                mBuilder.setContentIntent(pendingIntent);
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(455, mBuilder.build());

            String downloadedFile = AppPreferences.getDownloadReference(referenceId);
            snackDownloadedFile(referenceId, downloadedFile);
        }

    };

    private void snackDownloadedFile(final long referenceId, final String fileName) {
        Snackbar.make(librariesRecyclerView, fileName + " pdf download completed", Snackbar.LENGTH_INDEFINITE).setAction("OPEN", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryOpenFile(fileName);
                AppPreferences.saveDownloadReference(referenceId,null);
            }
        }).setAction("NOT NOW", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreferences.saveDownloadReference(referenceId,null);
            }
        }).show();
    }

    private void tryOpenFile(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/FinanceLearn/Libraries/" + fileName + ".pdf");
        if (file.exists()) {
            Intent launcherIntent = new Intent(Intent.ACTION_VIEW);
            launcherIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
            launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(launcherIntent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libraries);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        HashMap<String, String> ebankingPdf = new HashMap<>();
        ebankingPdf.put(FinanceLearningConstants.FILE_NAME, "E-banking");
        ebankingPdf.put("E-banking", "https://firebasestorage.googleapis.com/v0/b/financelearn-dffeb.appspot.com/o/Libraries%2Fe-bank.pdf?alt=media&token=8ebda546-2432-4d08-8050-217087fcec9f");

        HashMap<String, String> compliancePdf = new HashMap<>();

        compliancePdf.put(FinanceLearningConstants.FILE_NAME, "EMEAAP AC - Complaince Agent training - EN V1 (2)");
        compliancePdf.put("EMEAAP AC - Complaince Agent training - EN V1 (2)",
                "https://firebasestorage.googleapis.com/v0/b/financelearn-dffeb.appspot.com/o/Libraries%2FEMEAAP%20AC%20-%20Complaince%20Agent%20training%20-%20EN%20V1%20(2).pdf?alt=media&token=32fa54d4-8cc4-4be4-a218-dd5a8081fc1f");

        HashMap<String, String> loansAndCredit = new HashMap<>();
        loansAndCredit.put("Loans and Credit",
                "https://firebasestorage.googleapis.com/v0/b/financelearn-dffeb.appspot.com/o/Libraries%2FLoans%20and%20Credit.pdf?alt=media&token=3d2e016b-f4cf-4891-9db7-0d22c7603115");

        pdfLibraries.add(ebankingPdf);
        pdfLibraries.add(compliancePdf);
        pdfLibraries.add(loansAndCredit);

        librariesRecyclerView = (RecyclerView) findViewById(R.id.libraries_recycler_view);
        LibrariesAdapter librariesAdapter = new LibrariesAdapter(this, pdfLibraries);
        librariesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        librariesRecyclerView.setAdapter(librariesAdapter);

        registerReceiver(downloadsCompleteReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadsCompleteReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FinanceLearningConstants.STORAGE_PERMISSION_REQUEST_CODE && verifyPermissions(grantResults)) {
            if (FinanceLearningConstants.lastDownloadableFileMap != null
                    && !FinanceLearningConstants.lastDownloadableFileMap.isEmpty()
                    && FinanceLearningConstants.lastDownloadableFileName != null) {
                LibrariesAdapter.tryDownloadOpenFile(FinanceLearningConstants.lastDownloadableFileName,
                        PDFLibrariesRecyclerActivity.this,
                        FinanceLearningConstants.lastDownloadableFileMap);
            }
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
