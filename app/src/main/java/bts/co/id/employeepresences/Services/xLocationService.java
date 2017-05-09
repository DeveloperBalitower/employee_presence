package bts.co.id.employeepresences.Services;

//import android.location.LocationManager;

/**
 * Created by IT on 7/21/2016.
 */
//public class LocationService extends Service implements LocationListener {
//    public static double lat, lng;
//
//    private static volatile PowerManager.WakeLock wakeLock;
//    private static final int MILLISECONDS_PER_SECOND = 1000;
//    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
//    private static final long UPDATE_INTERVAL =
//            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
//    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
//    private static final long FASTEST_INTERVAL =
//            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
//
//    protected LocationManager locationManager;
//    protected LocationListener locationListener;
//    private GoogleApiClient mGoogleApiClient = null;
//
//    private boolean isGPSEnabled, isNetworkEnabled;
//    private Location location;
//    Notification.Builder builder;
//
//    public void onDestroy() {
//        Log.i("Location Services on Destroy");
//        if (mGoogleApiClient.isConnected()){
//            mGoogleApiClient.disconnect();
//        }
//        super.onDestroy();
//
//    }
//
//    @Override
//    public void onCreate() {
//        Log.i("Location Services on Create");
//        super.onCreate();
//        locationListener = new OnLocationListener();
////        locationRequest = LocationRequest.create();
////        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////        locationRequest.setInterval(UPDATE_INTERVAL);
////        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//        createGoogleApiClient();
////        mLocationClient = new LocationClient(this, this, this);
//        getLocation();
//    }
//
//    private void createGoogleApiClient() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
//    }
//
//    public void findAndSendLocation() {
//        if (wakeLock == null) {
//            PowerManager pm = (PowerManager)this.getSystemService(
//                    Context.POWER_SERVICE);
//
//			/* we don't need the screen on */
//            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    "triptracker");
//            wakeLock.setReferenceCounted(true);
//        }
//
//        if (!wakeLock.isHeld())
//            wakeLock.acquire();
//
////        LocationManager locationManager = (LocationManager)
////                this.getSystemService(Context.LOCATION_SERVICE);
////
////        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
////                locationListener, null);
//    }
//
//    public class OnLocationListener implements LocationListener{
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.i("disini");
//            updateWithNewLocation(location);
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    }
//
//    private void getLocation() {
//        Log.i("Location Services On GetLocation");
////        String context = Context.LOCATION_SERVICE;
////        locationManager = (LocationManager) getSystemService(context);
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//
//        if (locationManager != null) {
//            Log.i("locationManager Not Null");
//            isGPSEnabled = locationManager
//                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//            Log.i("GPS IS = "+String.valueOf(isGPSEnabled)+ " NETWORK_PROVIDER = "+ String.valueOf(isNetworkEnabled));
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                Log.e("No network provider is enabled ");
//            }else{
//                if (isNetworkEnabled){
////                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1,locationListener);
//                    Log.i("From Network");
//                    if (locationManager != null) {
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            if (builder != null){
//                                builder.setContentText("Your Location "+String.valueOf(location.getLatitude()) +" "+String.valueOf(location.getLongitude()));
//                                builder.notifyAll();
//                            }
//                            Log.i("Location Is Not Null");
//                        }else{
//                            Log.e("Location Is Null");
//                        }
//                    }
//                }
//
//                if (isGPSEnabled){
//                    if (location == null){
////                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,1, locationListener );
////                        if (locationManager != null){
////                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////                            if (location != null){
////                                if (builder != null) {
////                                    builder.setContentText("Your Location " + String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
////                                    builder.notifyAll();
////                                }
////                                Log.i("Location Is Not Null");
////                            }else{
////                                Log.i("Location Is Null");
////                            }
////                        }
//                    }
//                }
////                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
////                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            }
//        }else{
//            Log.e("LocationManager is Null");
//        }
//    }
//
//    public void updateWithNewLocation(Location loc) {
//        Log.i("Location Services On updateWithNewLocation");
//        Location location = loc;
//        if (loc != null) {
//            lat = loc.getLatitude();
//            lng = loc.getLongitude();
//            Intent broadCastIntent = new Intent(StaticData.OnUpdateLocationBroadCast);
//            broadCastIntent.setAction(StaticData.BroadCastUpdateAction);
//            broadCastIntent.putExtra("Latitude", lat);
//            broadCastIntent.putExtra("Longitude", lng);
//            broadCastIntent.putExtra("Provider", loc.getProvider());
//            sendBroadcast(broadCastIntent);
//            Log.i("updateWithNewLocation "+ lat+" - "+lng+" - "+loc.getProvider());
//        }else{
//            Log.e("Location Is Null");
//        }
//
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//}