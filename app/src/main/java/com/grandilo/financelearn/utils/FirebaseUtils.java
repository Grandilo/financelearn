package com.grandilo.financelearn.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

}
