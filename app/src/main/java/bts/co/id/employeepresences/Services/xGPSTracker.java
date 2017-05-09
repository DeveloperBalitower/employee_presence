package bts.co.id.employeepresences.Services;

/**
 * Created by Andreas Panjaitan on 7/25/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

//public class GPSTracker extends Service implements LocationListener {
//
//    private static final String TAG = "GPSTracker";
//
//    /**
//     * Register to receive callback on first fix status
//     *
//     * @author Morten
//     *
//     */
//    public interface FirstFixListener {
//
//        /**
//         * Is called whenever gps register a change in first-fix availability
//         * This is valuable to prevent sending invalid locations to the server.
//         *
//         * @param hasGPSfix
//         */
//        public void onFirsFixChanged(boolean hasGPSfix);
//    }
//
//    /**
//     * Register to receive all location updates
//     *
//     * @author Morten
//     *
//     */
//    public interface LocationUpdateListener {
//        /**
//         * Is called every single time the GPS unit register a new location
//         * The location param will never be null, however, it can be outdated if hasGPSfix is not true.
//         *
//         * @param location
//         */
//        public void onLocationChanged(Location location);
//    }
//
//    private Context mContext;
//
//    // flag for GPS status
//    private List<FirstFixListener> firstFixListeners;
//    private List<LocationUpdateListener> locationUpdateListeners;
//    boolean isGPSFix = false;
//    boolean isGPSEnabled = false;
//    private GPSFixListener gpsListener;
//    private FirstFixListener firstFixListener;
//    private LocationUpdateListener locationUpdateListener;
//
//    // flag for GPS status
//    boolean canGetLocation = false;
//
//    Location location; // location
//    double latitude; // latitude
//    double longitude; // longitude
//    long mLastLocationMillis;
//
//    private boolean logLocationChanges;
//
//    // Declaring a Location Manager
//    protected LocationManager locationManager;
//
//    /** removed again as we need multiple instances with different callbacks **/
//    private static GPSTracker instance;
//
//    @Override
//    public void onCreate() {
//        Log.i("Location Services on Create");
//        super.onCreate();
//        GPSTracker gpsTracker = GPSTracker.getInstance(getApplicationContext());
//        firstFixListener = new MyFirstFixListener();
//        locationUpdateListener = new MyLocationUpdateListener();
//
//        gpsTracker.startUsingGPS(firstFixListener, locationUpdateListener);
//
////        this.addLocationUpdateListener(new LocationUpdateListener() {
////            @Override
////            public void onLocationChanged(Location location) {
////                Log.e("OnLocationChanged : " + location.getLatitude() + " - " + location.getLongitude());
////            }
////        });
////        locationListener = new OnLocationListener();
////        locationRequest = LocationRequest.create();
////        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////        locationRequest.setInterval(UPDATE_INTERVAL);
////        locationRequest.setFastestInterval(FASTEST_INTERVAL);
////        createGoogleApiClient();
////        mLocationClient = new LocationClient(this, this, this);
//        getLocation();
//    }
//
//    public static GPSTracker getInstance(Context context) {
//        if (instance != null) {
//            return instance;
//        }
//        return instance = new GPSTracker(context);
//    }
//
//    private GPSTracker(Context context) {
//        this.mContext = context;
//        gpsListener = new GPSFixListener();
//        firstFixListeners = new ArrayList<FirstFixListener>();
//        locationUpdateListeners = new ArrayList<GPSTracker.LocationUpdateListener>();
//    }
//
//    public boolean hasGPSFirstFix() {
//        return isGPSFix;
//    }
//
//    private void addFirstFixListener(FirstFixListener firstFixListener) {
//        this.firstFixListeners.add(firstFixListener);
//    }
//
//    private void addLocationUpdateListener(
//            LocationUpdateListener locationUpdateListener) {
//        this.locationUpdateListeners.add(locationUpdateListener);
//    }
//
//    private void removeFirstFixListener(FirstFixListener firstFixListener) {
//        this.firstFixListeners.remove(firstFixListener);
//    }
//
//    private void removeLocationUpdateListener(
//            LocationUpdateListener locationUpdateListener) {
//        this.locationUpdateListeners.remove(locationUpdateListener);
//    }
//
//    public void setLogLocationChanges(boolean logLocationChanges) {
//        this.logLocationChanges = logLocationChanges;
//    }
//
//    public Location getLocation() {
//        return location;
//    }
//
//    private Location startLocationListener() {
//        canGetLocation = false;
//
//        try {
//            locationManager = (LocationManager) mContext
//                    .getSystemService(Service.LOCATION_SERVICE);
//
//            // getting GPS status
//            isGPSEnabled = locationManager
//                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            if (isGPSEnabled) {
//                if (location == null) {
//                    locationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER, 2, 7, this);
//                    locationManager.addGpsStatusListener(gpsListener);
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        if (location != null) {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                            Log.e(TAG,"latitude = "+String.valueOf(latitude));
//                            Log.e(TAG,"longitude = "+String.valueOf(longitude));
//                        }
//                    }
//                }
//            } else {
//                showSettingsAlert();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return location;
//    }
//
//    public void stopUsingGPS(FirstFixListener firstFixListener,
//                             LocationUpdateListener locationUpdateListener) {
//        if (firstFixListener != null)
//            removeFirstFixListener(firstFixListener);
//        if (locationUpdateListener != null)
//            removeLocationUpdateListener(locationUpdateListener);
//
//        stopUsingGPS();
//    }
//
//    /**
//     * Stop using GPS listener Calling this function will stop using GPS in your
//     * app
//     * */
//    public void stopUsingGPS() {
//        Log.d("DEBUG", "GPS stop");
//        if (locationManager != null) {
//            locationManager.removeUpdates(GPSTracker.this);
//            location = null;
//
//            if (gpsListener != null) {
//                locationManager.removeGpsStatusListener(gpsListener);
//            }
//
//        }
//        isGPSFix = false;
//        location = null;
//    }
//
//    public void startUsingGPS(FirstFixListener firstFixListener,
//                              LocationUpdateListener locationUpdateListener) {
//        Log.d("DEBUG", "GPS start");
//        if (firstFixListener != null)
//            addFirstFixListener(firstFixListener);
//        if (locationUpdateListener != null)
//            addLocationUpdateListener(locationUpdateListener);
//
//        startLocationListener();
//    }
//
//    /**
//     * Function to get latitude
//     * */
//    public double getLatitude() {
//        if (location != null) {
//            latitude = location.getLatitude();
//        } else {
//            Log.e("GPSTracker", "getLatitude location is null");
//        }
//
//        // return latitude
//        return latitude;
//    }
//
//    /**
//     * Function to get longitude
//     * */
//    public double getLongitude() {
//        if (location != null) {
//            longitude = location.getLongitude();
//        } else {
//            Log.e("GPSTracker", "getLongitude location is null");
//        }
//
//        // return longitude
//        return longitude;
//    }
//
//    /**
//     * Function to check GPS/wifi enabled
//     *
//     * @return boolean
//     * */
//    public boolean canGetLocation() {
//        return this.canGetLocation;
//    }
//
//    /**
//     * Function to show settings alert dialog On pressing Settings button will
//     * lauch Settings Options
//     * */
//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS settings");
//
//        // Setting Dialog Message
//        alertDialog
//                .setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(
//                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        mContext.startActivity(intent);
//                    }
//                });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        // Showing Alert Message
//        alertDialog.show();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if ( location == null)
//            return;
//
//        this.location = location;
//
//
//
//        mLastLocationMillis = SystemClock.elapsedRealtime();
//        canGetLocation = true;
//        if (isGPSFix) {
//
//
//            if (locationUpdateListeners != null) {
//                for (LocationUpdateListener listener : locationUpdateListeners) {
//                    listener.onLocationChanged(location);
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        canGetLocation = false;
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//    }
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return null;
//    }
//
//    private boolean wasGPSFix = false;
//
//    // http://stackoverflow.com/questions/2021176/how-can-i-check-the-current-status-of-the-gps-receiver
//// answer from soundmaven
//    private class GPSFixListener implements GpsStatus.Listener {
//        public void onGpsStatusChanged(int event) {
//            switch (event) {
//                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
//
//                    if (isGPSFix != wasGPSFix) { // only notify on changes
//                        wasGPSFix = isGPSFix;
//                        for (FirstFixListener listener : firstFixListeners) {
//                            listener.onFirsFixChanged(isGPSFix);
//                        }
//                    }
//
//                    break;
//                case GpsStatus.GPS_EVENT_FIRST_FIX:
//                    // Do something.
//
//
//
//                    break;
//            }
//        }
//    }
//
//
//    private class MyFirstFixListener implements FirstFixListener {
//
//        @Override
//        public void onFirsFixChanged(boolean hasGPSfix) {
//            if (hasGPSfix == true) {
//                Location position = getLocation();
//                // send SMS with position
//
//                // stop the gps and unregister callbacks
//                stopUsingGPS(firstFixListener, locationUpdateListener);
//            }
//
//        }
//
//    }
//
//    private class MyLocationUpdateListener implements LocationUpdateListener {
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.e("onLocationChanged = "+location.getLatitude()+ " "+ location.getLongitude());
//            // hand you each new location from the GPS
//            // you do not need this as you only want to send a single position
//
//        }
//
//    }
//}