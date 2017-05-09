package bts.co.id.employeepresences.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import bts.co.id.employeepresences.EmployeePresencesApplication;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Services.Controller.TrackServicesController;

/**
 * Created by Andreas Panjaitan on 10/11/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class xTrackServices extends Service {
    //    private static final int LOCATION_INTERVAL = 1000;
//    private static final float LOCATION_DISTANCE = 10;
    private static final int LOCATION_INTERVAL = 1000 * 60 * 1;
    private static final float LOCATION_DISTANCE = 10;
    public int counter = 0;
    long oldTime = 0;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;
    private TrackServicesController controller;
    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer ++++  " + (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
        Log.i("onStartCommand");
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
//        super.onCreate();
        android.os.Debug.waitForDebugger();
        Log.e("onCreate");
        initializeLocationManager();
//        controller = new TrackServicesController(this);

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e("fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.e("fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e("onDestroy");
//        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.e("fail to remove location listners, ignore", ex);
                }
            }
        }

        Intent intent = new Intent("bts.co.id.employeepresences.Receiver.ServiceStopReceiver");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
        stoptimertask();
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.e("GPS Is Enabled");
            }
        }
    }

    public EmployeePresencesApplication getEmployeeApplication() {
        return (EmployeePresencesApplication) getApplicationContext();
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
            Log.e("LocationListener " + provider + " location = " + mLastLocation.getLatitude() + " , " + mLastLocation.getLongitude());
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e("onLocationChanged: " + location);
            mLastLocation.set(location);
            controller.onLocationChangeController(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("onStatusChanged: " + provider);
        }
    }
}