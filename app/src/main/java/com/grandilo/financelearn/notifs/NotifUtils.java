package com.grandilo.financelearn.notifs;

import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.HashMap;

/**
 * @author Ugo
 */

public class NotifUtils {

    public static void blowNotification(HashMap<String, Object> notifProps) {

        String notifCategory = (String) notifProps.get(FinanceLearningConstants.NOTIFICATION_CATEGORY);
        String notifBody = (String) notifProps.get(FinanceLearningConstants.NOTIFICATION_BODY);

        if (notifCategory.equals(FinanceLearningConstants.CATEGORY_ASSIGN_ME_COURSES)) {

            String employeeName = (String) notifProps.get(FinanceLearningConstants.EMPLOYEE_NAME);
            String employeeId = (String) notifProps.get(FinanceLearningConstants.EMPLOYEE_ID);

        }

    }

}
