package bts.co.id.employeepresences.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bts.co.id.employeepresences.Activity.View.HomeActivity;
import bts.co.id.employeepresences.BroadcastReceiver.AlarmBroadcast;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.R;


/**
 * Created by Andreas Panjaitan on 8/1/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class xTrackingServices extends Service {

    private static final String LOG_TAG = "xTrackingServices";
    //    private List<LocationTrackingListener> mListeners;
    private static final String CELL_PROVIDER_TAG = "cell";

    // controls which location providers to track
//    private Set<String> mTrackedProviders;

    //    private TrackerDataHelper mTrackerData;
    // signal strength updates
    private static final String SIGNAL_PROVIDER_TAG = "signal";
    private static final String WIFI_PROVIDER_TAG = "wifi";
    // tracking tag for data connectivity issues
    private static final String DATA_CONN_PROVIDER_TAG = "data";
    public static xTrackingServices trackingServices;
    private static volatile PowerManager.WakeLock wakeLock;
    private static boolean isRunning = false;
    private TelephonyManager mTelephonyManager;
    PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCellLocationChanged(CellLocation location) {
            Log.i("On Cell Location Changed");
            try {
                if (location instanceof GsmCellLocation) {
                    GsmCellLocation cellLocation = (GsmCellLocation) location;
                    String updateMsg = "cid=" + cellLocation.getCid() +
                            ", lac=" + cellLocation.getLac();
//                    mTrackerData.writeEntry(CELL_PROVIDER_TAG, updateMsg);
                } else if (location instanceof CdmaCellLocation) {
                    CdmaCellLocation cellLocation = (CdmaCellLocation) location;
                    String updateMsg = "BID=" + cellLocation.getBaseStationId() +
                            ", SID=" + cellLocation.getSystemId() +
                            ", NID=" + cellLocation.getNetworkId() +
                            ", lat=" + cellLocation.getBaseStationLatitude() +
                            ", long=" + cellLocation.getBaseStationLongitude() +
                            ", SID=" + cellLocation.getSystemId() +
                            ", NID=" + cellLocation.getNetworkId();
//                    mTrackerData.writeEntry(CELL_PROVIDER_TAG, updateMsg);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception in CellStateHandler.handleMessage:", e);
            }
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.i("On Signal Strengths Changed");
            if (mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                String updateMsg = "cdma dBM=" + signalStrength.getCdmaDbm();
//                mTrackerData.writeEntry(SIGNAL_PROVIDER_TAG, updateMsg);
            } else if (mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                String updateMsg = "gsm signal=" + signalStrength.getGsmSignalStrength();
//                mTrackerData.writeEntry(SIGNAL_PROVIDER_TAG, updateMsg);
            }
        }
    };
    private Location mNetworkLocation;
    // Handlers and Receivers for phone and network state
    private NetworkStateBroadcastReceiver mNetwork;
    private GlobalManager globalManager;
    private LocationListener locationListener;
    private AlarmManager alarmManager;
    private PendingIntent pendingAlarm;
    private int freqSeconds;
    private String freqString;
    private NotificationManager nm;
    //    private PreferenceListener mPrefListener;
    private Notification notification;
    private Notification.Builder notifBuilder;

    public xTrackingServices() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        xTrackingServices.trackingServices = this;
        Log.i("Services On Create");
        getGlobalManager();
        mNetworkLocation = null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateNotification("Location Change : " + location.getLatitude() + "," + location.getLongitude());
                Log.i("On Location Change = " + location.getLatitude() + "," + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                updateNotification("On Status Changed " + s);
                Log.i("On Status Changed " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                updateNotification("On Provider Enabled " + s);
                Log.i("On Provider Enabled " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                updateNotification("On Provider Disabled " + s);
                Log.i("On Provider Disabled " + s);
            }
        };

        freqSeconds = 0;
        freqString = null;

        long minUpdateTime = this.getGlobalManager().getSharedPreferencesManager().getLocationUpdateTime();
        freqString = String.valueOf(minUpdateTime);
//        float minDistance = this.getGlobalManager().getSharedPreferencesManager().getLocationMinDistance();
        Log.i("Min Update = " + minUpdateTime);
//        Log.i("Min Distance = " + minDistance);
//
////        freqString = Prefs.getUpdateFreq(this);
//        if (freqString != null && !freqString.equals("")) {
//            try {
//                Pattern p = Pattern.compile("(\\d+)(m|h|s)");
//                Matcher m = p.matcher(freqString);
//                m.find();
//                freqSeconds = Integer.parseInt(m.group(1));
//                if (m.group(2).equals("h"))
//                    freqSeconds *= (60 * 60);
//                else if (m.group(2).equals("m"))
//                    freqSeconds *= 60;
//            }
//            catch (Exception e) {
//            }
//        }
//        initLocationListeners();
        showNotification();
        isRunning = true;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmBroadcast.class);
        pendingAlarm = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), minUpdateTime, pendingAlarm);
        findAndSendLocation();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // ignore - nothing to do
        return null;
    }


//    /**
//     * registers location listeners
//     *
//     * @param intent
//     * @param startId
//     */


//    @Override
//    public void onStart(Intent intent, int startId) {
//
//        Log.i("On Start");
//        Toast.makeText(this, "Tracking service started", Toast.LENGTH_SHORT);
//
//        super.onStart(intent, startId);
////        this.globalManager.getSharedPreferencesManager().setUserLoginPref();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("On Start Command");
        if (this.globalManager.getSharedPreferencesManager().isServicesRuning() == false) {
            this.globalManager.getSharedPreferencesManager().setTrackingServices(true);
        }
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private synchronized void initLocationListeners() {
        Log.i("Init Location Listener");
//        mTrackerData = new TrackerDataHelper(this);
//        LocationManager lm = getLocationManager();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        mTrackedProviders = getTrackedProviders();

//        List<String> locationProviders = locationManager.getAllProviders();
//        mListeners = new ArrayList<LocationTrackingListener>(
//                locationProviders.size());
        long minUpdateTime = this.getGlobalManager().getSharedPreferencesManager().getLocationUpdateTime();
        float minDistance = this.getGlobalManager().getSharedPreferencesManager().getLocationMinDistance();
        Log.i("Min Update = " + minUpdateTime);
        Log.i("Min Distance = " + minDistance);
//        for (String providerName : locationProviders) {
//            if (mTrackedProviders.contains(providerName)) {
//                Log.i("Adding location listener for provider " +
//                        providerName);
//                if (this.getGlobalManager().getSharedPreferencesManager().doDebugLogging()) {
//                    Log.i("Init, "+ String.format(
//                            "start listening to %s : %d ms; %f meters",
//                            providerName, minUpdateTime, minDistance));
//                }
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Log.e("Location can't be request update location ::: Permision Issue");
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
////                    return;
//                }
//
//
//                Location location = locationManager.getLastKnownLocation(providerName);
//
////                LocationTrackingListener listener =
////                        new LocationTrackingListener();
//                if (location != null) {
//                    Log.i("last known location of this "+providerName+" is "+location.getLatitude()+","+location.getLongitude());
//                    locationListener.onLocationChanged(location);
//                }else{
////                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                    startActivity(intent);
//                    Log.i("Can't get location from "+providerName);
////                    Intent dialogIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(dialogIntent);
//                }
////                locationManager.requestLocationUpdates(providerName, minUpdateTime,
////                        minDistance, listener);
//                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
//                        locationListener, null);
////                mListeners.add(listener);
//            }
//        }
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (this.getGlobalManager().getSharedPreferencesManager().doDebugLogging()) {
            // register for cell location updates
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);

            // Register for Network (Wifi or Mobile) updates
            mNetwork = new NetworkStateBroadcastReceiver();
            IntentFilter mIntentFilter;
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            Log.i("registering receiver");
            registerReceiver(mNetwork, mIntentFilter);
        }

        if (this.getGlobalManager().getSharedPreferencesManager().trackSignalStrength()) {
            mTelephonyManager.listen(mPhoneStateListener,
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }

        // register for preference changes, so we can restart listeners on
        // pref changes
//        mPrefListener = new PreferenceListener();
//        getPreferences().registerOnSharedPreferenceChangeListener(mPrefListener);
    }

//    private SharedPreferences getPreferences() {
//        return PreferenceManager.getDefaultSharedPreferences(this);
//    }

    private Set<String> getTrackedProviders() {
        Log.i("Get Tracked Providers");
        Set<String> providerSet = new HashSet();

        if (this.getGlobalManager().getSharedPreferencesManager().trackGPS()) {
            providerSet.add(LocationManager.GPS_PROVIDER);
        }
        if (this.getGlobalManager().getSharedPreferencesManager().trackNetwork()) {
            providerSet.add(LocationManager.NETWORK_PROVIDER);
        }
        return providerSet;
    }

    /**
     * De-registers all location listeners, closes persistent storage
     */
//    protected synchronized void stopListeners() {
//        Log.i("Stop Listener");
//        LocationManager lm = getLocationManager();
//        if (mListeners != null) {
//            for (LocationListener listener : locationListener) {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Log.e("Location can't be remove update location ::: Permision Issue");
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
////                    return;
//                }
//                lm.removeUpdates(locationListener);
//            }
//            mListeners.clear();
//        }
//        mListeners = null;
//
//        // stop cell state listener
//        if (mTelephonyManager != null) {
//            mTelephonyManager.listen(mPhoneStateListener, 0);
//        }
//
//        // stop network/wifi listener
//        if (mNetwork != null) {
//            unregisterReceiver(mNetwork);
//        }
//        mNetwork = null;
//
////        mTrackerData = null;
////        if (mPrefListener != null) {
////            getPreferences().unregisterOnSharedPreferenceChangeListener(mPrefListener);
////            mPrefListener = null;
////        }
//    }

//    private LocationManager getLocationManager() {
//        Log.i("Get Location Manager");
//        return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    }

    /**
     * Shuts down this service
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Removing location listeners");
        try {
            LocationManager locationManager = (LocationManager)
                    this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
        }
        getGlobalManager().getSharedPreferencesManager().setTrackingServices(false);
        Toast.makeText(this, "Tracking service stopped", Toast.LENGTH_SHORT);
        isRunning = false;
    }

//    private class LocationTrackingListener implements LocationListener {
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.i("On Location Change = "+location.getLatitude()+","+location.getLongitude());
//            // Initialize the location fields
////            latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
////            longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
////            provText.setText(provider + " provider has been selected.");
////
////            Toast.makeText(MainActivity.this,  "Location changed!",
////                    Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.i(provider + "'s status changed to "+status +"!");
////            Toast.makeText(MainActivity.this, provider + "'s status changed to "+status +"!",
////                    Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            Log.i("Provider " + provider + " enabled!");
////            Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",
////                    Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            Log.e("Provider " + provider + " disabled!");
////            Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!",
////                    Toast.LENGTH_SHORT).show();
//        }
//    }

//    private class LocationTrackingListener implements LocationListener {
//
//        /**
//         * Writes details of location update to tracking file, including
//         * recording the distance between this location update and the last
//         * network location update
//         *
//         * @param location - new location
//         */
//        public void onLocationChanged(Location location) {
//            Log.i("Location On Change to "+location.getLatitude()+" : "+location.getLongitude());
//            if (location == null) {
//                return;
//            }
//            float distance = getDistanceFromNetwork(location);
////            mTrackerData.writeEntry(location, distance);
//        }
//
//        /**
//         * Writes update to tracking file
//         *
//         * @param provider - name of disabled provider
//         */
//        public void onProviderDisabled(String provider) {
//            Log.i("On Provider Disabled");
//            if (getGlobalManager().getSharedPreferencesManager().doDebugLogging()) {
////                mTrackerData.writeEntry(provider, "provider disabled");
//            }
//        }
//
//        /**
//         * Writes update to tracking file
//         *
//         * @param provider - name of enabled provider
//         */
//        public void onProviderEnabled(String provider) {
//            Log.i("On Provider Enabled");
//            if (getGlobalManager().getSharedPreferencesManager().doDebugLogging()) {
////                mTrackerData.writeEntry(provider,  "provider enabled");
//            }
//        }
//
//        /**
//         * Writes update to tracking file
//         *
//         * @param provider - name of provider whose status changed
//         * @param status - new status
//         * @param extras - optional set of extra status messages
//         */
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.i("On Status Changed");
//            if (getGlobalManager().getSharedPreferencesManager().doDebugLogging()) {
////                mTrackerData.writeEntry(provider,  "status change: " + status);
//            }
//        }
//    }

    /**
     * Determine the current distance from given location to the last
     * approximated network location
     *
     * @param location - new location
     * @return float distance in meters
     */
    private synchronized float getDistanceFromNetwork(Location location) {
        Log.i("Get Distance From Network");
        float value = 0;
        if (mNetworkLocation != null) {
            value = location.distanceTo(mNetworkLocation);
        }
        if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
            mNetworkLocation = location;
        }
        return value;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public GlobalManager getGlobalManager() {
        Log.i("Get Global Manager");
        if (this.globalManager == null) {
            this.globalManager = new GlobalManager(this);
        }
        return this.globalManager;
    }

//    private class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
//
//        public void onSharedPreferenceChanged(
//                SharedPreferences sharedPreferences, String key) {
//            Log.d(LOG_TAG, "restarting listeners due to preference change");
//            synchronized (xTrackingServices.this) {
//                stopListeners();
//                initLocationListeners();
//            }
//        }
//    }

    public void findAndSendLocation() {
        Log.i("Find And Send Location Function");
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(
                    Context.POWER_SERVICE);

			/* we don't need the screen on */
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "triptracker");
            wakeLock.setReferenceCounted(true);
        }

        if (!wakeLock.isHeld())
            wakeLock.acquire();

        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                locationListener, null);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.i("Check Location");
        if (location != null) {
            Log.i("Last Location = " + location.getLatitude() + "," + location.getLongitude());
        } else {
            Log.e("Location is Null");
        }
    }

    private void showNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        notifBuilder = new Notification.Builder(getApplicationContext());
        notifBuilder.setAutoCancel(false);
        notifBuilder.setTicker("this is ticker text");
        notifBuilder.setContentTitle(getString(R.string.app_name));
        notifBuilder.setContentText("sending location every " + freqString);
        notifBuilder.setSmallIcon(R.drawable.ic_launcher);
        notifBuilder.setContentIntent(contentIntent);
        notifBuilder.setOngoing(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notifBuilder.setSubText(getString(R.string.app_name) + " sending location every "+freqString);   //API level 16
//        }
        notifBuilder.setNumber(100);
//        notifBuilder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notifBuilder.build();
        } else {
            notifBuilder.getNotification();
        }

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notification = notifBuilder.getNotification();
        nm.notify(11, notification);

//        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        notification = new Notification(R.mipmap.ic_launcher,
//                "Trip Tracker Started", System.currentTimeMillis());
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, HomeActivity.class), 0);
//        notification.setLatestEventInfo(this, "Trip Tracker",
//                "Sending location every " + freqString, contentIntent);
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        nm.notify(1, notification);
    }

    private void updateNotification(String text) {
        if (nm != null) {
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, HomeActivity.class), 0);
            notifBuilder.setContentText(text);
            notification = notifBuilder.getNotification();
            nm.notify(11, notification);
//            notification.setLatestEventInfo(this, "Trip Tracker", text,
//                    contentIntent);
//            notification.when = System.currentTimeMillis();
//            nm.notify(1, notification);
        }
    }

    /**
     * Listener + recorder for mobile or wifi updates
     */
    private class NetworkStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Broadcast On Receive");
            String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                WifiManager wifiManager =
                        (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> wifiScanResults = wifiManager.getScanResults();
                String updateMsg = "num scan results=" +
                        (wifiScanResults == null ? "0" : wifiScanResults.size());
//                mTrackerData.writeEntry(WIFI_PROVIDER_TAG, updateMsg);

            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                String updateMsg;
                boolean noConnectivity =
                        intent.getBooleanExtra(
                                ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if (noConnectivity) {
                    updateMsg = "no connectivity";
                } else {
                    updateMsg = "connection available";
                }
//                mTrackerData.writeEntry(DATA_CONN_PROVIDER_TAG, updateMsg);

            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);

                String stateString = "unknown";
                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        stateString = "disabled";
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        stateString = "disabling";
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        stateString = "enabled";
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        stateString = "enabling";
                        break;
                }
//                mTrackerData.writeEntry(WIFI_PROVIDER_TAG,
//                        "state = " + stateString);
            }
        }
    }
}
