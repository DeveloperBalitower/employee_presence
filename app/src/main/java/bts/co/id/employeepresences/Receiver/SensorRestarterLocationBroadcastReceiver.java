package bts.co.id.employeepresences.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bts.co.id.employeepresences.Manager.Log;

/**
 * Created by Andreas Panjaitan on 8/5/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class SensorRestarterLocationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service Stops! Oooooooooooooppppssssss!!!!");
//        context.startService(new Intent(context, TrackingService.class));
        ;
    }
}