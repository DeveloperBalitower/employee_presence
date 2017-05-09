package bts.co.id.employeepresences.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

import bts.co.id.employeepresences.EmployeePresencesApplication;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;

/**
 * Created by Andreas Panjaitan on 8/4/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class xLocationTrackingByGoogle extends Service {

    public static xLocationTrackingByGoogle instances;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    EmployeePresencesApplication application;
    private GlobalManager globalManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private List<GeofenceModel> geofenceList;
    private List<String> geofencesIdsList;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            globalManager.writeTextFile("Battery Level = " + String.valueOf(level) + "%");
            if (application.isPhonePluggedIn(getApplicationContext()).compareToIgnoreCase("yes") == 0) {
                globalManager.writeTextFile("Charging:yes");
            } else {
                globalManager.writeTextFile("Charging:no");
            }

            if (level < 25 & application.isPhonePluggedIn(getApplicationContext()).compareTo("yes") == 0) {
//                Intent alarmIntent = new Intent("android.intent.action.MAIN");
//                alarmIntent.setClass(xLocationTrackingByGoogle.this, HomeScreenDialogActivity.class);
//                alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                alarmIntent.putExtra("messages","Your Battery is "+ level+"%"+" please charges your phone");
//                xLocationTrackingByGoogle.this.startActivity(alarmIntent);
            } else {
//                Intent alarmIntent = new Intent("android.intent.action.MAIN");
//                alarmIntent.setClass(xLocationTrackingByGoogle.this, HomeScreenDialogActivity.class);
//                alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                alarmIntent.putExtra("messages","Your Battery is "+ level+"%"+" please charges your phone");
//                xLocationTrackingByGoogle.this.startActivity(alarmIntent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        android.os.Debug.waitForDebugger();
        xLocationTrackingByGoogle.instances = this;
        this.application = (EmployeePresencesApplication) getApplication();
        this.globalManager = this.application.getGlobalManager();

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        geofenceList = new ArrayList<>();
        geofencesIdsList = new ArrayList<>();
//        SmartLocation.with(this).location()
//                .start(new OnLocationUpdatedListener() {
//                    @Override
//                    public void onLocationUpdated(Location location) {
//                        application.writeTextFile("Move "+location.getLatitude()+","+location.getLongitude());
//                        if (globalManager.checkMockLocation() == false) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                                if (location.isFromMockProvider()) {
//                                    globalManager.showNotification("Please disable your mock location and refresh this application", instances);
//                                    Intent dialogIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(dialogIntent);
//                                    Intent dialogMockIntent = new Intent().setClassName("com.android.settings", "com.android.settings.DevelopmentSettings");
//                                    dialogMockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                //            startActivity(dialogIntent);
//                                    startActivity(dialogMockIntent);
//                //                    checkLocation();
//                                }
//                            }
//                        }
//
//                        if (geofenceList.size() > 0){
//                            geofenceList.clear();
//                        }
//
//                        if (geofencesIdsList.size() > 0){
//                            SmartLocation.with(instances).geofencing()
//                                    .removeAll(geofencesIdsList)
//                                    .start(new OnGeofencingTransitionListener() {
//                                        @Override
//                                        public void onGeofenceTransition(TransitionGeofence transitionGeofence) {
//                                            return;
//                                        }
//                                    });
//                            geofencesIdsList.clear();
//                        }
//
//                        geofenceList = application.getGeofenceList(location);
//
//                        geofencesIdsList = application.getGeofencesIdsList();
//                        SmartLocation.with(instances).geofencing().addAll(geofenceList).start(new OnGeofencingTransitionListener() {
//                            @Override
//                            public void onGeofenceTransition(TransitionGeofence transitionGeofence) {
//                                if (transitionGeofence.getTransitionType() == Geofence.GEOFENCE_TRANSITION_ENTER){
//                                    application.writeTextFile("CheckIn GF Transition :  "+transitionGeofence.getTransitionType()+","+transitionGeofence.getGeofenceModel().getRequestId());
//                                    globalManager.showNotification("Checkin", instances);
//                                }
//
//                                else if (transitionGeofence.getTransitionType() == Geofence.GEOFENCE_TRANSITION_EXIT){
//                                    application.writeTextFile("CheckOut GF Transition :  "+transitionGeofence.getTransitionType()+","+transitionGeofence.getGeofenceModel().getRequestId());
//                                    globalManager.showNotification("Check Out", instances);
//                                }
//                                application.writeTextFile("GF Transation :  "+transitionGeofence.getTransitionType()+","+transitionGeofence.getGeofenceModel().getRequestId());
//                            }
//                        });
//                    }
//                });
    }

//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        globalManager.showNotification("Location services is running", this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("services Destroy");
        globalManager.showNotification("Location services is stopped", this);
        Intent intent = new Intent("bts.co.id.employeepresences.Receiver.ServiceStopReceiver");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(instances)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();

    }

//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if (globalManager.checkMockLocation() == false) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                if (location.isFromMockProvider()) {
//                    globalManager.showNotification("Please disable your mock location and refresh this application", this);
//                    Intent dialogIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(dialogIntent);
//                    Intent dialogMockIntent = new Intent().setClassName("com.android.settings", "com.android.settings.DevelopmentSettings");
//                    dialogMockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            startActivity(dialogIntent);
//                    startActivity(dialogMockIntent);
////                    checkLocation();
//                }
//            }
//        }
//
//        globalManager.showNotification("Loc Change "+location.getLatitude()+","+location.getLongitude(), this);
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }

    private boolean checkGooglePlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            /*
            * google play services is missing or update is required
			*  return code could be
			* SUCCESS,
			* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
			* SERVICE_DISABLED, SERVICE_INVALID.
			*/
//            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
//                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

//    private void initDbManager() {
//        if (!DatabaseManager.getInstance().isDatabaseExists()) {
//            DatabaseManager.getInstance().createDatabase(this.getApplicationContext());
//            DatabaseManager.getInstance().openDatabase();
//            DatabaseManager.getInstance().initDatabase();
//        }
//    }
}
