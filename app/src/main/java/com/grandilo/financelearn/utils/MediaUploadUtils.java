package com.grandilo.financelearn.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grandilo.financelearn.R;
import com.grandilo.financelearn.callbacks.DoneCallback;
import com.grandilo.financelearn.ui.ApplicationLoader;
import com.grandilo.financelearn.ui.UploadCourseVideoActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.grandilo.financelearn.utils.FirebaseUtils.getFirebaseStorageReference;

/**
 * @author Ugo
 */

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
public class MediaUploadUtils {

    private static final String TAG = "MeidaUploadUtils";
    private static final String MAIN_FOLDER_META_DATA = "FinanceLearn";
    private static final String OTHER_FILES_FOLDER = "Others";
    private static final String IMAGES_FOLDER = "Images";
    private static final String VIDEOS_FOLDER = "Videos";
    private static final String _THUMBNAIL_SUFFIX = "_thumbnail";
    private static final String IMAGE_DIR = "Images";

    public static void uploadVideoAndPushCourse(final Map<String, Object> tobeUploadedFiles, final HashMap<String, Object> courseProps) {

        final Map<String, Object> uploadedFiles = new HashMap<>();
        Iterator<Object> hashMapIterator = tobeUploadedFiles.values().iterator();

        do {
            final Map<String, Object> uploadableFile = (HashMap<String, Object>) hashMapIterator.next();
            final String localFilePath = (String) uploadableFile.get(FinanceLearningConstants.MEDIA_LOCAL_URL);

            uploadFileAsync(localFilePath.hashCode(), "FinanceLearn Video Upload", "Uploading Video", localFilePath, FinanceLearningConstants.VIDEO_DIRECTORY,
                    new DoneCallback<String>() {
                        @Override
                        public void done(final String uploadedFileUrl, Exception e) {
                            if (e == null) {
                                if (uploadedFileUrl != null) {
                                    uploadableFile.put(FinanceLearningConstants.REMOTE_URL, uploadedFileUrl);
                                }
                                final Bitmap videoThumbnail = generateVideoThumbnail(localFilePath);
                                if (videoThumbnail != null) {
                                    final File file = saveBitmap(videoThumbnail, generateRandomString());
                                    if (file.exists()) {
                                        Log.d("ThumbnailLog", "Thumbnail File Exists");
                                        uploadFileAsync(videoThumbnail.hashCode(), "FinanceLearn Video Thumbnail Upload", "Uploading Video Thumbnail", file.getPath(), FinanceLearningConstants.THUMBNAILS_DIRECTORY,
                                                new DoneCallback<String>() {
                                                    @Override
                                                    public void done(String result, Exception e) {
                                                        if (e == null) {
                                                            if (result != null) {
                                                                Log.d("ThumbnailLog", "Result of video Video Upload = " + result);
                                                                if (uploadableFile.containsKey(FinanceLearningConstants.THUMB_NAIL)) {
                                                                    uploadableFile.remove(FinanceLearningConstants.THUMB_NAIL);
                                                                }
                                                                uploadableFile.put(FinanceLearningConstants.THUMB_NAIL, result);
                                                                file.delete();
                                                            } else {
                                                                Log.d("ThumbnailLog", "Result of video Video Upload = null");
                                                            }
                                                        } else {
                                                            Log.d("ThumbnailLog", "An exception occurred in video upload");
                                                            getNotificationManager().cancel(videoThumbnail.hashCode());
                                                            UiUtils.showToast("Failed to upload Video Thumbnail");
                                                        }
                                                        uploadedFiles.put(FinanceLearningConstants.COURSE_VIDEO, uploadableFile);
                                                        Log.d(TAG, "Size of uploaded files = " + uploadedFiles.size());
                                                        if (uploadedFiles.size() == tobeUploadedFiles.size()) {
                                                            pushCourse(courseProps, uploadedFiles);
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.d("ThumbnailLog", "Video Thumbnail file does not exist");
                                        UiUtils.showToast("Failed to upload Video Thumbnail");
                                        getNotificationManager().cancel(videoThumbnail.hashCode());
                                    }
                                } else {
                                    Log.d("ThumbnailLog", "Video Thumbnail bitmap file is null");
                                    UiUtils.showToast("Failed to upload Video Thumbnail");
                                    getNotificationManager().cancel(localFilePath.hashCode());
                                }
                            } else {
                                Log.e(TAG, e.getMessage());
                                UiUtils.showToast("Failed to upload Video");
                                getNotificationManager().cancel(localFilePath.hashCode());
                            }
                        }
                    });
        } while (hashMapIterator.hasNext());
    }

    private static void pushCourse(HashMap<String, Object> courseProps, Map<String, Object> uploadedFiles) {
        courseProps.putAll(uploadedFiles);
        FirebaseUtils.getCourseVideosReference().push().setValue(courseProps, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    //Course video was uploaded successfully
                    Toast.makeText(ApplicationLoader.getInstance(), "Course Video Uploaded successfully", Toast.LENGTH_LONG).show();
                } else {
                    //Failed to upload course video
                    Toast.makeText(ApplicationLoader.getInstance(), "Failed to upload course video. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Save course here
    }

    private static File saveBitmap(Bitmap bitmap, String filename) {
        File file = getFilePath(filename + ".png", ApplicationLoader.getInstance(), "image", false);
        try {
            OutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;
    }

    private static File getFilePath(String fileName, Context context, String contentType, boolean isThumbnail) {
        File filePath;
        File dir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String folder = "/" + getMetaDataValue(context, MAIN_FOLDER_META_DATA) + OTHER_FILES_FOLDER;
            if (contentType.startsWith("image")) {
                folder = "/" + getMetaDataValue(context, MAIN_FOLDER_META_DATA) + IMAGES_FOLDER;
            } else if (contentType.startsWith("video")) {
                folder = "/" + getMetaDataValue(context, MAIN_FOLDER_META_DATA) + VIDEOS_FOLDER;
            }
            if (isThumbnail) {
                folder = folder + _THUMBNAIL_SUFFIX;
            }
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            ContextWrapper cw = new ContextWrapper(context);
            dir = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        }
        filePath = new File(dir, fileName);
        return filePath;
    }

    private static String getMetaDataValue(Context context, String metaDataName) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                return ai.metaData.getString(metaDataName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Bitmap generateVideoThumbnail(String mediaLocalUrl) {
        return ThumbnailUtils.createVideoThumbnail(mediaLocalUrl, MediaStore.Video.Thumbnails.MINI_KIND);
    }

    private static String generateRandomString() {
        return System.currentTimeMillis() + "_";
    }

    private static void uploadFileAsync(final int operationId, final String title, final String progressMessage, final String filePath, String directory, final DoneCallback<String> doneCallback) {
        Uri uri = Uri.fromFile(new File(filePath));
        StorageReference storageReference = getFirebaseStorageReference().child(directory).child(uri.getLastPathSegment());

        UploadTask uploadTask = storageReference.putFile(uri.normalizeScheme());

        FinanceLearningConstants.taskQueue.put(operationId, uploadTask);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("unused")
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                updateProgressNotification(operationId, title, (int) progress, progressMessage);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getDownloadUrl() != null) {
                    String returnedFileUrl = taskSnapshot.getDownloadUrl().toString();
                    Log.e(TAG, "Completed Upload of file = " + filePath + " with upload url = " + returnedFileUrl);
                    FinanceLearningConstants.taskQueue.remove(operationId);
                    doneCallback.done(returnedFileUrl, null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "An error occurred while uploading file. Error message = " + e.getMessage());
                FinanceLearningConstants.taskQueue.remove(operationId);
                doneCallback.done(null, e);
            }

        });

    }


    public static NotificationManager getNotificationManager() {
        return (NotificationManager) ApplicationLoader.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static void updateProgressNotification(int operationId, String title, int progress, String progressMessage) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ApplicationLoader.getInstance());
        NotificationManager notificationManager = getNotificationManager();

        mBuilder.setContentTitle(title)
                .setContentText(progressMessage)
                .setSmallIcon(android.R.drawable.stat_sys_upload);

        mBuilder.setProgress(100, progress, false);

        notificationManager.notify(operationId, mBuilder.build());

        mBuilder.setOngoing(true);
        mBuilder.setContentInfo(progress + "%");

        if (progress == 100) {
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Upload Complete")
                    // Removes the progress bar
                    .setProgress(0, 0, false);
            mBuilder.setOngoing(false);
            mBuilder.setSmallIcon(android.R.drawable.stat_sys_upload_done);
        } else {
            Intent cancelIntent = new Intent(ApplicationLoader.getInstance().getApplicationContext(), UploadCourseVideoActivity.class);
            cancelIntent.putExtra(FinanceLearningConstants.CANCEL_UPLOAD, true);
            cancelIntent.putExtra(FinanceLearningConstants.OPERATION_ID, operationId);
            PendingIntent cancelPendingIntent = PendingIntent.getActivity(ApplicationLoader.getInstance().getApplicationContext(), 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_clear_black_24dp, "CANCEL", cancelPendingIntent);
        }
        notificationManager.notify(operationId, mBuilder.build());
    }

}
