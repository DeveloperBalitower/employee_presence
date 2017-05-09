package bts.co.id.employeepresences.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Andreas Panjaitan on 10/4/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
//            Intent pushIntent = new Intent(context, TrackingService.class);
//            context.startService(pushIntent);
        }
    }
}