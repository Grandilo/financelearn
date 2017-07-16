package com.grandilo.financelearn.notifs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.text.Html;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.ApplicationLoader;
import com.grandilo.financelearn.ui.activities.EmployeeHomeScreen;
import com.grandilo.financelearn.ui.activities.StaffProfileManagementScreen;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;

/**
 * @author Ugo
 */

@SuppressWarnings("deprecation")
public class NotifUtils {

    public static void blowNotification(HashMap<String, Object> notifProps) {

        Context context = ApplicationLoader.getInstance();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        String notifCategory = (String) notifProps.get(FinanceLearningConstants.NOTIFICATION_CATEGORY);
        Intent notifIntent = new Intent();

        android.support.v7.app.NotificationCompat.BigTextStyle bigTextStyle = new android.support.v7.app.NotificationCompat.BigTextStyle(builder);
        bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
        if (notifCategory.equals(FinanceLearningConstants.CATEGORY_ASSIGN_ME_COURSES)) {
            notifIntent = new Intent(context, StaffProfileManagementScreen.class);
            String employeeName = (String) notifProps.get(FinanceLearningConstants.EMPLOYEE_NAME);
            String employeeId = (String) notifProps.get(FinanceLearningConstants.EMPLOYEE_ID);
            builder.setContentText(Html.fromHtml("<b>" + employeeName + "</b> has requested you to assign him/her two test courses"));
            bigTextStyle.bigText(Html.fromHtml("<b>" + employeeName + "</b> has requested you to assign him/her two test courses"));
            notifIntent.putExtra(FinanceLearningConstants.LAUNCH_TYPE, FinanceLearningConstants.LAUNCH_TYPE_ASSIGN_COURSES);
            notifIntent.putExtra(FinanceLearningConstants.EMPLOYEE_ID, employeeId);
            notifIntent.putExtra(FinanceLearningConstants.EMPLOYEE_NAME,employeeName);
        } else if (notifCategory.equals(FinanceLearningConstants.CATEGORY_COURSES_ASSIGNED_TO_ME_JUST_NOW)) {
            builder.setContentText("You've being assigned 2 courses by your manager. You may proceed to finishing up your test");
            bigTextStyle.bigText("You've being assigned 2 courses by your manager. You may proceed to finishing up your test");
            notifIntent = new Intent(context, EmployeeHomeScreen.class);
        }
        builder.setAutoCancel(true);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setStyle(bigTextStyle);
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        if (ApplicationLoader.NOTIFICATION_COUNT == 0) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        int NOTIF_CODE = 9;
        notificationManager.notify(NOTIF_CODE, notification);
        ApplicationLoader.NOTIFICATION_COUNT++;

    }

}
