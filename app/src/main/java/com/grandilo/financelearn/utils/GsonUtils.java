package com.grandilo.financelearn.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author Ugo
 */

public class GsonUtils {

    private static Gson getGson() {
        return new Gson();
    }

    public static String getHashMarshall(HashMap<String, Object> marshable) {
        Type hashType = getHashTypeToken();
        Gson gson = getGson();
        return gson.toJson(marshable, hashType);
    }

    private static HashMap<String, Object> getHashFromString(String stringMarshal) {
        Type hashType = getHashTypeToken();
        return getGson().fromJson(stringMarshal, hashType);
    }

    private static Type getHashTypeToken() {
        return new TypeToken<HashMap<String, Object>>() {
        }.getType();
    }

    public static String getCourseName(String courseId) {
        String courseMapString = AppPreferences.getCourseMapString(courseId);
        if (courseMapString != null) {
            HashMap<String, Object> courseProps = getHashFromString(courseMapString);
            if (courseProps != null) {
                return (String) courseProps.get(FinanceLearningConstants.COURSE_NAME);
            }
        }
        return null;
    }

    public static HashMap<String, Object> getCourseProps(String courseId) {
        String courseMapString = AppPreferences.getCourseMapString(courseId);
        if (courseMapString != null) {
            return getHashFromString(courseMapString);
        }
        return null;
    }

}
