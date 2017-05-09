package bts.co.id.employeepresences.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bts.co.id.employeepresences.Manager.Log;

/**
 * Created by Andreas Panjaitan on 8/1/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /* we now have a wake lock until we return */
        Log.i("OnReceive");
//        if (xTrackingServices.trackingServices != null && xTrackingServices.trackingServices.isRunning()){
//            Log.i("Run Fin And Send Location");
//            xTrackingServices.trackingServices.findAndSendLocation();
//        }else{
//            Log.i("xTrackingServices.trackingServices != null && xTrackingServices.trackingServices.isRunning == false");
//        }

    }
}
