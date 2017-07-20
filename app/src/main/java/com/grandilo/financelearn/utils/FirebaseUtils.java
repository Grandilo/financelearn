package com.grandilo.financelearn.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * @author Ugo
 */

public class FirebaseUtils {

    public static DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getStaffReference() {
        return getRootReference().child("staff");
    }

    public static DatabaseReference getCourses() {
        return getRootReference().child("courses");
    }

    public static DatabaseReference getNotificationsNode(){
        return getRootReference().child(FinanceLearningConstants.NOTIFICATIONS_NODE);
    }

    public static DatabaseReference getPretestReference(){
        return getRootReference().child(FinanceLearningConstants.PRETEST);
    }

    public static DatabaseReference getCourseVideosReference(){
        return getRootReference().child(FinanceLearningConstants.COURSE_VIDEOS);
    }

    public static StorageReference getFirebaseStorageReference() {
        return FirebaseStorage.getInstance().getReferenceFromUrl(FinanceLearningConstants.FINANCE_LEARN_FILES_BUCKET);
    }

}
