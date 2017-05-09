package bts.co.id.employeepresences.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Andreas Panjaitan on 10/5/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class ServiceStopReceiver extends BroadcastReceiver {

    private final String TAG = "ServiceStopReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "services is stopped, argghhh");
//        context.startService(new Intent(context, TrackingService.class));
        ;
    }

}
