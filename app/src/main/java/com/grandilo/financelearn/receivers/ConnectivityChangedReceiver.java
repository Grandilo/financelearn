package com.grandilo.financelearn.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grandilo.financelearn.PushReceptionService;

/**
 * @author Ugo
 */

public class ConnectivityChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pushRecptionService = new Intent(context, PushReceptionService.class);
        context.startService(pushRecptionService);
    }

}
