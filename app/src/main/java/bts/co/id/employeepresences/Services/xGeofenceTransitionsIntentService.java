package bts.co.id.employeepresences.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import bts.co.id.employeepresences.Manager.Log;

/**
 * Created by Andreas Panjaitan on 8/3/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class xGeofenceTransitionsIntentService extends IntentService {
    public xGeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return xGeofenceTransitionsIntentService.getErrorString(context, GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return xGeofenceTransitionsIntentService.getErrorString(context, GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES);
//                return mResources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return xGeofenceTransitionsIntentService.getErrorString(context, GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS);
//                return mResources.getString(R.string.geofence_too_many_pending_intents);
            default:
//                return xGeofenceTransitionsIntentService.getErrorString(context, GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS);
                return "Unknown Geofences ERROR";
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = xGeofenceTransitionsIntentService.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );

            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails);
//            Log.i(geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(geofenceTransition);
        }
    }
}
