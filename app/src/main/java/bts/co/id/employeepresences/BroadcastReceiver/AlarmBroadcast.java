package bts.co.id.employeepresences.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bts.co.id.employeepresences.Services.xTrackerService;

/**
 * Created by Andreas Panjaitan on 7/25/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /* we now have a wake lock until we return */

        if (xTrackerService.service != null &&
                xTrackerService.service.isRunning())
            xTrackerService.service.findAndSendLocation();
    }
}

