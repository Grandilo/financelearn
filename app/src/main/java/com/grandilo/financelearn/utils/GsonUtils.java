package com.grandilo.financelearn.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Ugo
 */

public class GsonUtils {

    public static Gson getGson() {
        return new Gson();
    }

    public static String getHashMarshall(HashMap<String, Object> marshable) {
        Type hashType = getHashTypeToken();
        Gson gson = getGson();
        return gson.toJson(marshable, hashType);
    }

    public static HashMap<String, Object> getHashFromString(String stringMarshal) {
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
