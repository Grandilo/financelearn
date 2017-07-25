package com.grandilo.financelearn.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Ugo
 */

public class EmailUtils {

    private static String TAG = EmailUtils.class.getSimpleName();

    public static void sendEmail(Context context, List<String> recommendedCourses) {

        JSONObject signedInUserProps = AppPreferences.getSignedInUser(context);

        if (signedInUserProps != null) {
            String lastName = signedInUserProps.optString(FinanceLearningConstants.LAST_NAME);
            String surname = signedInUserProps.optString(FinanceLearningConstants.SURNAME);

            String name = lastName + " " + surname;

            BackgroundMail.newBuilder(context)
                    .withUsername("financelearnacademy@gmail.com")
                    .withPassword("ugochukwu100%")
                    .withMailto("wannclem@gmail.com")
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject("FinanceLearn Recommended Courses for " + name)
                    .withBody("Haven completed the Pretest and Main Test in partial fulfilment of the requirements for FinanceLearn, here are the courses recommended for the candidate " + name.toUpperCase() + "\n\n" + TextUtils.join(", ", recommendedCourses))
                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            //do some magic
                            AppPreferences.saveEmailSentStatus(true);
                            Log.d(TAG,"Email Sent Successfully");
                        }
                    })
                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                        @Override
                        public void onFail() {
                            //do some magic
                            AppPreferences.saveEmailSentStatus(true);
                            Log.d(TAG,"Failed to send email");
                        }
                    })
                    .send();
        }

    }

}
