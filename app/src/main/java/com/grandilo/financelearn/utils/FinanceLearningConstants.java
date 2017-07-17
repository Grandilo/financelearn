package com.grandilo.financelearn.utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * This holds commonly used app constants
 *
 * @author Ugo
 */
public class FinanceLearningConstants {

    public static final String LOGIN_TYPE = "login_type";
    public static final String LOGIN_TYPE_EMPLOYEE = "login_type_staff";
    public static final String LOGIN_TYPE_MANAGER = "login_type_manager";
    public static final String LOGIN_TYPE_HR = "login_type_hr";
    public static final String EMAIL_DOMAIN_SUFFIX = "@app.com";

    public static final String LAST_NAME = "lastname";
    public static final String SURNAME = "surname";
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String COURSE_NAME = "name";

    public static final String LOGGED_IN_USER = "logged_in_user";
    public static final String PASSWORD = "password";

    public static final String COURSE_ID = "course_id";
    public static final String CATEGORY_ASSIGN_ME_COURSES = "assign_me_courses";
    public static final String EMPLOYEE_NAME = "employee_name";
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String LAUNCH_TYPE_ASSIGN_COURSES = "launch_type_assign_courses";

    public static final String LAUNCH_TYPE = "launch_type";
    public static final String CATEGORY_COURSES_ASSIGNED_TO_ME_JUST_NOW = "courses_assigned_to_me_just_now";
    public static final java.lang.String SOURCE_ACTIVITY = "source_activity";
    public static final String NOTIFICATIONS_TARGET = "notification_target";
    public static final String ACTION_TYPE = "action_type";

    public static final String ACTION_TYPE_COURSE_SELECTION = "action_type_course_selection";
    public static final String SELECTED_COURSES_COUNT = "selected_courses_count";
    public static final String SELECTED_PRE_TEST_COURSES = "selected_pre_test_courses";
    public static final String PRETEST = "pre_test";

    public static final java.lang.String QUESTION = "question";
    public static final String OPTIONS = "options";
    public static final String ANSWER = "answer";
    public static final String TOTAL_NO_OF_QS = "no_of_qs";

    public static String NOTIFICATIONS_NODE = "Notifications";

    public static String NOTIFICATION_BODY = "notification_body";
    public static String NOTIFICATION_CATEGORY = "notification_category";

    public static String COURSES_ASSIGNED = "courses_assigned";


    //////PRETEST COURSE MAP/////
    public static HashMap<String, String> courseMap = new HashMap<>();

    ///////PRETEST RESULT MAP//////////
    //<CourseId, Right Answers Map>
    public static HashMap<String, List<JSONObject>> rightAnswersMap = new HashMap<>();

    //<CourseId, Wrong Answers Map>
    public static HashMap<String, List<JSONObject>> wrongAnswersMap = new HashMap<>();

}
