package bts.co.id.employeepresences.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import bts.co.id.employeepresences.Model.StaticData;

/**
 * Created by Andreas Panjaitan on 8/3/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class SiteGeofenceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // Intent contains information about errors in adding or removing geofences
        if (TextUtils.equals(action, StaticData.ACTION_GEOFENCE_ERROR)) {
        } else if (TextUtils.equals(action, StaticData.ACTION_GEOFENCES_ADDED) || TextUtils.equals(action, StaticData.ACTION_GEOFENCES_REMOVED)) {
            // handleGeofenceStatus(context, intent);

        } else if (TextUtils.equals(action, StaticData.ACTION_GEOFENCE_TRANSITION)) {

            // handleGeofenceTransition(context, intent);

        } else {
            // handle error

        }
    }
}
