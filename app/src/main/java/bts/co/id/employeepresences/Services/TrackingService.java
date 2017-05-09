package bts.co.id.employeepresences.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bts.co.id.employeepresences.EmployeePresencesApplication;
import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Manager.DatabaseManager;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import bts.co.id.employeepresences.Model.StaticData;
import bts.co.id.employeepresences.Model.Workplace;
import bts.co.id.employeepresences.Services.Controller.TrackingServiceController;
import okhttp3.Request;

//import android.util.Log;
//import android.location.LocationListener;
//import android.nfc.Tag;

/**
 * Created by Andreas Panjaitan on 8/1/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class TrackingService extends Service implements LocationListener {


    final long update_location_time = 500;
    //    public String TAG = "TrackingService_LOG";
    private LocationManager locationManager;
    private GlobalManager globalManager;
    private EmployeePresencesApplication application;
    private Location location;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.location.PROVIDERS_CHANGED")) {
                Log.i("PROVIDERS_CHANGED");
                onCreate();
            } else if (action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                //action for phone state changed
                Log.i("ACTION_PHONE_STATE_CHANGED");
            }
        }
    };
    //    private List<NearbySite> nearbySites = new ArrayList<>();
    private TrackingServiceController controller;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("On Create");
//        android.os.Debug.waitForDebugger();
        application = (EmployeePresencesApplication) getApplicationContext();
        globalManager = application.getGlobalManager();
        checkLocation();

    }

    @Override
    public void onLocationChanged(Location location) {
        android.util.Log.i("onLocationChange","lat=" + location.getLatitude() + ",long=" + location.getLongitude() + ",alt=" + location.getAltitude() + ",acc=" + location.getAccuracy() + ",bear=" + location.getBearing() + ",prov=" + location.getProvider());
        Log.i("On Location Changed To lat=" + location.getLatitude() + ",long=" + location.getLongitude() + ",alt=" + location.getAltitude() + ",acc=" + location.getAccuracy() + ",bear=" + location.getBearing() + ",prov=" + location.getProvider());
        if (isCheckIn()) {
            final HistoryDataModel historyDataModel = getGlobalManager().getSharedPreferencesManager().getHistoryDataModel();
            Workplace workplace = DatabaseManager.getInstance().getWorkPlace(historyDataModel.getSite());
            float[] resultsDistanceByLocation = new float[1];
            Location.distanceBetween(Double.valueOf(workplace.getLatitude()), Double.valueOf(workplace.getLongitude()), location.getLatitude(), location.getLongitude(), resultsDistanceByLocation);
            if (resultsDistanceByLocation[0] > StaticData.DISTANCES) {
                // Check Out User
                final Date dateCheckOutDaate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd:mm:ss");
                historyDataModel.setCheck_out_date(dateFormat.format(dateCheckOutDaate));
                historyDataModel.setCheck_out_time(timeFormat.format(dateCheckOutDaate));
                historyDataModel.setCheckout_lat(location.getLatitude());
                historyDataModel.setCheckout_long(location.getLongitude());

                String dateCheckIn = historyDataModel.getCheck_in_date() + " " + historyDataModel.getCheck_in_time();
                String dateCheckOut = historyDataModel.getCheck_out_date() + " " + historyDataModel.getCheck_out_time();
//                PresencesHistory history;
                final List<HistoryDataModel> historyDataModelList = new ArrayList<>();
                getGlobalManager().getApiManager().user_checkOut(historyDataModel.getSite(), historyDataModel.getServer_id(), dateCheckIn, dateCheckOut, new ApiListener() {
                    @Override
                    public void onFailure(Request request, IOException e) {
//                        getApplicationContext()
                        historyDataModelList.add(historyDataModel);
                        DatabaseManager.getInstance().insertPresencesHistory(historyDataModelList, 0);
                        getGlobalManager().getSharedPreferencesManager().checkOutPreferences();
                        Intent intent = new Intent();
                        intent.setAction("bts.co.id.employeepresencest.UserCheckOutBroadCast");
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onResponseSuccess(String responseBody) throws IOException {
                        historyDataModelList.add(historyDataModel);
                        DatabaseManager.getInstance().insertPresencesHistory(historyDataModelList, 1);
                        getGlobalManager().getSharedPreferencesManager().checkOutPreferences();
                        Intent intent = new Intent();
                        intent.setAction("bts.co.id.employeepresencest.UserCheckOutBroadCast");
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onResponseError(String response) {
                        historyDataModelList.add(historyDataModel);
                        DatabaseManager.getInstance().insertPresencesHistory(historyDataModelList, 0);
                        getGlobalManager().getSharedPreferencesManager().checkOutPreferences();
                        Intent intent = new Intent();
                        intent.setAction("bts.co.id.employeepresencest.UserCheckOutBroadCast");
                        sendBroadcast(intent);
                    }
                });
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        getGlobalManager().writeTextFile("xTrackingServices onTaskRemoved");
        Log.e("onTaskRemoved");
        getGlobalManager().writeTextFile("xTrackingServices is destroyed");
        Intent intent = new Intent("bts.co.id.employeepresences.Receiver.ServiceStopReceiver");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

//    public void stoptimertask() {
//        //stop the timer, if it's not already null
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }

//    public void startTimer() {
//        //set a new Timer
//        timer = new Timer();
//
//        //initialize the TimerTask's job
//        initializeTimerTask();
//
//        //schedule the timer, to wake up every 1 second
//        timer.schedule(timerTask, 1000, 10000); //
//    }
//
//    public void initializeTimerTask() {
//        timerTask = new TimerTask() {
//            public void run() {
//                Log.i("in timer", "in timer ++++  "+ (counter++));
//            }
//        };
//    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy");
//        if (locationManager != null) {
//            for (int i = 0; i < locationManager.length; i++) {
//                try {
//                    locationManager.removeUpdates(locationManager[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
//                }
//            }
//        }
        getGlobalManager().writeTextFile("xTrackingServices is destroyed");
        Intent intent = new Intent("bts.co.id.employeepresences.Receiver.ServiceStopReceiver");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
//        stoptimertask();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getGlobalManager().writeTextFile("TrackingService is running startid = " + startId);
        updateNotification("Tracking Services Started");
        receiverDetectGPSChange();
    }

    private void receiverDetectGPSChange() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.location.PROVIDERS_CHANGED");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        checkLocation();
//        updateNotification("On Status Changed "+ s);
        Log.i("On Status Changed " + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        checkLocation();
//        updateNotification("On Provider Enabled "+ s);
        Log.i("On Provider Enabled " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
//        checkLocation();
        updateNotification("GPS Provider is Disabled " + s);
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(intent);
        Log.i("On Provider Disabled " + s);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("On Bind");
        return null;
    }

    public void updateNotification(String text) {
        Log.i("update notification = " + text);
        startForeground(1234, globalManager.showNotification(text, this));
    }


    private void checkLocation() {
        Log.i("checkLocation");
        if (this.isCheckIn()) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                Log.d("Removing Test providers");
                if (globalManager.checkMockLocation()) {
                    locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
                }
            } catch (IllegalArgumentException error) {
                Log.d("Got exception in removing test  provider");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("CheckPermision");
                updateNotification("GPS permission failed");
                return;
            }
            locationManager.removeUpdates(this);
            location = null;

            if (globalManager.getUserManager().checkUserLogin()) {
                Log.i("disini 2");
                if (location == null) {
                    Log.i("disini 3");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            update_location_time,   // 3 sec
                            0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.i("checkLocation location == null => requestLocationUpdates last_location = " + location.getLatitude() + "," + location.getLongitude());
                        this.onLocationChanged(location);
                    } else {
                        Log.e("checkLocation failed to get location by gps");
                    }
                }

                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            update_location_time,   // 3 sec
                            0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.i("checkLocation location == null => requestLocationUpdates last_location = " + location.getLatitude() + "," + location.getLongitude());
                        this.onLocationChanged(location);
                    } else {
                        Log.e("checkLocation failed to get location by gps");
                        Intent dialogIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);
                    }
                }

                if (location == null) {
                    updateNotification("Can't get location GPS");
                    checkLocation();
                }
            }
        }
    }

    private List<Workplace> getProcessingWorkPlaces(List<Workplace> workPlaces, int distance, Location userLocation) {
        try {

            double userLat = userLocation.getLatitude();
            double userLng = userLocation.getLongitude();

            Log.i("User Lat = " + userLat + "," + userLng);

            if (workPlaces == null) {
                workPlaces = new ArrayList<>();
                workPlaces = application.getGlobalManager().getDatabaseManager().getWorkplaces(userLat, userLng, distance);
                Log.i("workplaces Size = " + workPlaces.size());
            }
//            List<Workplace> workPlaceCheckIn = new ArrayList<>();

            updateNotification("Check in process");
            Log.i("WorkPlaces Size = " + workPlaces.size());


            if (workPlaces == null && workPlaces.size() < 0) {
                Log.i("Workplaces == null && size < 0");
//                application.getGlobalManager().getApiManager().getDatabaseManager()
            }

            if (workPlaces != null && workPlaces.size() > 1) {

//                Iterator<Workplace> itWorkPlaces = workPlaces.iterator();
                while (workPlaces.size() > 1) {
                    distance = distance - 5;
                    Log.i("WorkPlaces Size = " + workPlaces.size() + " distances " + distance);
                    int ix = 0;

                    for (int wi = workPlaces.size() - 1; wi > 0; wi--) {
                        Workplace workplace = workPlaces.get(wi);
//                        Log.i("Loop For Iterator = "+ix++);
                        double venueLat = Double.parseDouble(workplace.getLatitude().replace(',', '.'));
                        double venueLng = Double.parseDouble(workplace.getLongitude().replace(',', '.'));
                        Double distanceToVenue = globalManager.getDistancesPositionOnMeters(userLat, userLng, venueLat, venueLng);
//                        Log.i(" check Distances to Venue "+ distanceToVenue.intValue());
                        if (distanceToVenue.intValue() > distance) {
                            Log.i(" it WorkPlaces is Remover cause  distances to venue is larger than " + distance);
                            if (workPlaces.size() > 1) {
                                workPlaces.remove(wi);
                            }
                        }
                    }

                    Log.i("End Of for final size =  " + workPlaces.size());
                }
            }

        } catch (NumberFormatException e) {
            // p did not contain a valid double
            updateNotification("NumberFormatException: " + e.getMessage());
            onCreate();
        }
        return workPlaces;
    }

    private Boolean isCheckIn() {
        if (globalManager.getUserManager().checkUserLogin() && getGlobalManager().getSharedPreferencesManager().getIsCheckin() && !getGlobalManager().getSharedPreferencesManager().getHistoryDataJSON().isEmpty() && getGlobalManager().getSharedPreferencesManager().getHistoryDataModel() != null) {
            return true;
        }
//        if (historyDataModel == null) {
//            return false;
//        } else {
//            if (historyDataModel.getCheck_in_date() != null && historyDataModel.getCheck_in_time() != null) {
//                return true;
//            }
//
//            String[] dateString = globalManager.getCurrentDateTimeArray();
//            Log.i("Check CheckIn Date " + historyDataModel.getCheck_in_date() + " " + dateString[1]);
//            if (!historyDataModel.getCheck_in_date().equals (dateString[1])) {
//                return true;
//            }
//        }
        return false;
    }

//    private void locationProcess(Location location) {
//        if (!getGlobalManager().isDatabaseExists()) {
//            return;
//        }
//
//        Double distaceDouble = globalManager.getDatabaseManager().getDistancesCheckIn();
//        int distance = distaceDouble.intValue();
//        Log.d("Check Distance " + distance);
//        List<Workplace> workPlaces = getProcessingWorkPlaces(null, distance, location);
//
//        while (workPlaces.size() > 1) {
//            workPlaces = getProcessingWorkPlaces(workPlaces, distance, location);
//        }
//
//        final HistoryDataModel historyDataModel = getGlobalManager().getSharedPreferencesManager().getHistoryDataModel();
//
//        if (historyDataModel == null || !getGlobalManager().getSharedPreferencesManager().getIsLogin() || !isCheckIn(historyDataModel)) {
//            Log.i("User Can't checkin");
//            if (workPlaces.size() == 1) {
//                checkInUser(workPlaces.get(0));
//            }
//        } else {
////            User Checkout
//            if (workPlaces != null && workPlaces.size() > 0) {
//                updateNotification("You are in " + workPlaces.get(0).getWorkplaceName());
//                if (workPlaces.size() == 1) {
//                    double userLat = location.getLatitude();
//                    double userLng = location.getLongitude();
//                    Workplace workplaceCheckIn = getGlobalManager().getDatabaseManager().getDataQuery(Workplace.class, "SELECT * FROM " + DatabaseManager.TABLE_WORKPLACES + " where workplace_id = '" + historyDataModel.getSite() + "';");
//                    double venueLat = Double.parseDouble(workplaceCheckIn.getLatitude().replace(',','.'));
//                    double venueLng = Double.parseDouble(workplaceCheckIn.getLongitude().replace(',','.'));
//
//                    Log.i("Venue Location " + venueLat + " " + venueLng);
//                    Double distanceToVenue = globalManager.getDistancesPositionOnMeters(userLat, userLng, venueLat, venueLng);
//                    getGlobalManager().writeTextFile("User Checkout process venue location = " + venueLat + " , " + venueLng + " distance " + distanceToVenue);
//                    Log.i("Venue Distance " + distanceToVenue);
//                    Log.i("Workplace Id Checking " + workPlaces.get(0).getWorkplaceId() + " == " + historyDataModel.getSite());
//                    getGlobalManager().writeTextFile("User Checkout process Workplace Id Checking " + workPlaces.get(0).getWorkplaceId() + " == " + historyDataModel.getSite());
//
//                    String[] dateString = globalManager.getCurrentDateTimeArray();
//
//
//                    if (!workPlaces.get(0).getWorkplaceId().equals(historyDataModel.getSite())){
//                        Log.i("User Check Out Process because "+workPlaces.get(0).getWorkplaceId()+ " != "+historyDataModel.getSite());
//
//                        historyDataModel.setCheck_out_date(dateString[1]);
//                        historyDataModel.setCheck_out_time(dateString[2]);
//                        userCheckOut(historyDataModel, dateString[0]);
//                    }
//
//                    else if (!historyDataModel.getCheck_in_date().equals(dateString[1])){
//                        Log.i("User Check Out Process because "+historyDataModel.getCheck_in_date() + " Not Equals = "+dateString[1]);
//                        historyDataModel.setCheck_out_date(dateString[1]);
//                        historyDataModel.setCheck_out_time(dateString[2]);
//                        userCheckOut(historyDataModel, dateString[0]);
//                    }
//
//                    else if (distanceToVenue.intValue() > distance){
//                        Log.i("User Check Out Process because "+distanceToVenue.intValue() + " > "+distance);
//                        if (historyDataModel.getServer_id() == null) {
//                            historyDataModel.setServer_id(0);
//                        }
//
//                        historyDataModel.setCheck_out_date(dateString[1]);
//                        historyDataModel.setCheck_out_time(dateString[2]);
//                        userCheckOut(historyDataModel,dateString[0]);
//                    }
//                }
//            }
//        }
//    }

    private void userCheckOut(final HistoryDataModel historyDataModel, String date) {
        globalManager.getApiManager().user_checkOut(historyDataModel.getSite(), historyDataModel.getServer_id(), historyDataModel.getCheck_in_date() + " " + historyDataModel.getCheck_in_time(), date, new ApiListener() {
            @Override
            public void onFailure(Request request, IOException e) {
                globalManager.getDatabaseManager().checkOutUser(historyDataModel.getId(), historyDataModel.getServer_id(), historyDataModel.getSite(), historyDataModel.getCheck_out_date(), historyDataModel.getCheck_out_time(), 0);
                globalManager.getSharedPreferencesManager().checkOutPreferences();
            }

            @Override
            public void onResponseSuccess(String responseBody) throws IOException {
                Log.i("User CheckOut Api Success " + responseBody);
                RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);
                historyDataModel.setServer_id(return_data.getData().getCheckinData().getId());
                globalManager.getDatabaseManager().checkOutUser(historyDataModel.getId(), historyDataModel.getServer_id(), historyDataModel.getSite(), historyDataModel.getCheck_out_date(), historyDataModel.getCheck_out_time(), 1);
                globalManager.getSharedPreferencesManager().checkOutPreferences();
            }

            @Override
            public void onResponseError(String response) {
                globalManager.getDatabaseManager().checkOutUser(historyDataModel.getId(), historyDataModel.getServer_id(), historyDataModel.getSite(), historyDataModel.getCheck_out_date(), historyDataModel.getCheck_out_time(), 0);
                globalManager.getSharedPreferencesManager().checkOutPreferences();
            }
        });
    }

//    private void checkInUser(Workplace workplace) {
//        Log.d("proccessing CheckIn " + workplace.getWorkplaceName());
//        getController().userCheckIn(workplace);
//    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            Log.i("currentBestLocation == null");
            // A new location is always better than no location
            return true;
        } else {
            Log.i("currentBestLocation != null");
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        Log.i("timeDelta " + location.getTime() + "-" + currentBestLocation.getTime() + " " + timeDelta + " " + update_location_time);
        boolean isSignificantlyNewer = timeDelta > update_location_time;
        boolean isSignificantlyOlder = timeDelta < -update_location_time;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            Log.i("isSignificantlyNewer");
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            Log.i("isSignificantlyOlder");
            return false;
        }
        Log.i("disini");

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

//    private void checkInProcess() {
//        if (nearbySites.size() > 0) {
//            Log.i("NearBy > 0");
//        }
//    }

//    private void checkInProcess(NearbySite nearbySite){
//        if (nearbySites.size() > 0){
//            boolean yesInsert = true;
//            for (int i = 0; i < nearbySites.size(); i++){
//                if (nearbySites.get(i).getSiteId() == nearbySite.getSiteId()){
//                    yesInsert = false;
//                    break;
//                }
//            }
//            if (yesInsert){
//                nearbySites.add(nearby
// Site);
//                return;
//            }
//        }else{
//            nearbySites.add(nearbySite);
//            return;
//        }
//    }

//    public EmployeePresencesApplication getEmployApplication() {
//        return application;
//    }

//    private double getDistancesPositionOnMeters(double userLat, double userLng, double venueLat, double venueLng){
//        double latDistance = Math.toRadians(userLat - venueLat);
//        double lngDistance = Math.toRadians(userLng - venueLng);
//        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
//                (Math.cos(Math.toRadians(userLat))) *
//                        (Math.cos(Math.toRadians(venueLat))) *
//                        (Math.sin(lngDistance / 2)) *
//                        (Math.sin(lngDistance / 2));
//
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//        double dist = 6371 * c;
//
//        double distMeters = dist * 100;
//        DecimalFormat twoDForm = new DecimalFormat("#.##");
//        return Double.valueOf(twoDForm.format(distMeters));
//    }

    public GlobalManager getGlobalManager() {
        if (globalManager == null) {
            globalManager = new GlobalManager(this);
        }
        return globalManager;
    }

    public TrackingServiceController getController() {
        if (controller == null) {
            controller = new TrackingServiceController(this);
        }
        return controller;
    }
}
