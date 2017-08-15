package com.grandilo.financelearn.utils;

import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This holds commonly used app constants
 *
 * @author Ugo
 */
public class FinanceLearningConstants {

    public static final String BASIC = "Basic";
    public static final String INTERMEDIATE = "Intermediate";
    public static final String EXPERT = "Expert";
    public static final String EXPERTISE_LEVEL = "expertise_level";
    public static final String COURSE_DETAILS = "course_details";
    public static final String COURSE_FILES = "course_files";
    public static final String REMOTE_URL = "remote_url";
    public static final String COURSE_VIDEO = "course_video";
    public static final String CANCEL_UPLOAD = "cancel_upload";
    public static final String OPERATION_ID = "operation_id";
    public static final String PRETEST_TAKEN = "pretest_taken";
    public static final String ALL_PRETEST_COURSES = "all_pretest_courses";
    public static final java.lang.String MAIN_TEST_TAKEN = "main_test_taken";
    public static final String PRETEST_RIGHT_ANSWERS = "pretest_right_answers";
    public static final String PRETEST_WRONG_ANSWERS = "pretest_wrong_answers";
    public static final java.lang.String VIDEO_URL = "video_url";
    public static final java.lang.String VIDEO_NAME = "video_name";
    public static final String MAIN_TEST = "main_test";

    public static final String MAIN_TEST_RIGHT_ANSWERS = "main_test_right_answers";
    public static final String MAIN_TEST_WRONG_ANSWERS = "main_test_wrong_answers";
    public static final String EMAIL_SENT = "email_sent";
    public static final java.lang.String STAFF_HR_ID = "staff_hr_id";
    public static final java.lang.String STAFF_MANAGER_ID = "staff_manager_id";
    public static final java.lang.String LOGGED_IN_STATUS = "logged_in";
    public static final String LOGGED_IN = "logged_in";
    public static final String LOGIN_TYPE_GUEST = "log_in_type_guest";

    public static final String FILE_NAME = "file_name";
    public static final String FILE_PATH = "file_path";

    public static String COURSE_VIDEOS = "course_videos";

    public static final String LOGIN_TYPE = "login_type";
    public static final String LOGIN_TYPE_EMPLOYEE = "login_type_staff";
    public static final String LOGIN_TYPE_MANAGER = "login_type_manager";

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
    public static final String SELECTED_PRE_TEST_COURSES = "selected_pre_test_courses";
    public static final String PRETEST = "pre_test";

    public static final java.lang.String QUESTION = "question";
    public static final String OPTIONS = "options";
    public static final String ANSWER = "answer";
    public static final String TOTAL_NO_OF_QS = "no_of_qs";
    public static final String MEDIA_LOCAL_URL = "media_local_url";
    public static final String VIDEO_DIRECTORY = "Videos";
    public static final String THUMBNAILS_DIRECTORY = "VideoThumbnails";
    public static final String THUMB_NAIL = "thumb_nail_url";

    public static String NOTIFICATIONS_NODE = "Notifications";
    public static String NOTIFICATION_CATEGORY = "notification_category";

    public static String COURSES_ASSIGNED = "courses_assigned";
    public static final String FINANCE_LEARN_FILES_BUCKET = "gs://financelearn-dffeb.appspot.com";

    public static HashMap<String, String> courseIdNameMap = new HashMap<>();
    public static HashMap<String, HashMap<String, Object>> fullCourseDetailsMap = new HashMap<>();

    ///////PRETEST RESULT MAP//////////

    //<CourseId, Right Answers Map>
    public static HashMap<String, List<JSONObject>> pretestRightAnswers = new HashMap<>();
    public static HashMap<String, List<JSONObject>> mainTestRightAnswers = new HashMap<>();

    //<CourseId, Wrong Answers Map>
    public static HashMap<String, List<JSONObject>> pretestWrongAnswers = new HashMap<>();
    public static HashMap<String, List<JSONObject>> mainTestWrongAnswers = new HashMap<>();

    public static HashMap<String, Boolean> pickedOptions = new HashMap<>();

    public static ArrayList<String> coursesToTest = new ArrayList<>();
    public static ArrayList<String> coursesToAssign = new ArrayList<>();

    public static SparseArray<UploadTask> taskQueue = new SparseArray<>();
    public static HashMap<String, Float> mainTestScoresMap = new HashMap<>();

    public static String PRETEST_PROFICIENCY_LEVEL_BASIC = "basic";
    public static String PRETEST_PROFICIENCY_LEVEL_INTERMEDIATE = "intermediate";
    public static String PRETEST_PROFICIENCY_LEVEL_EXPERT = "expert";

    public static String MAIN_PROFICIENCY_LEVEL_BELOW_AVERAGE = "below_average";
    public static String MAIN_PROFICIENCY_LEVEL_AVERAGE = "average";
    public static String MAIN_PROFICIENCY_LEVEL_GOOD = "good";

    public static HashMap<String, ArrayList<String>> recommendationsMap = new HashMap<>();

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 0x10;
    public static HashMap<String, String> lastDownloadableFileMap = new HashMap<>();
    public static String lastDownloadableFileName;
}
