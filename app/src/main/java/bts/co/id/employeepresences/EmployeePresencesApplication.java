package bts.co.id.employeepresences;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bts.co.id.employeepresences.Manager.DatabaseManager;
import bts.co.id.employeepresences.Manager.GPSHelper;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.ServicesLog;
import bts.co.id.employeepresences.Model.StaticData;
import bts.co.id.employeepresences.Model.Workplace;
import bts.co.id.employeepresences.Services.TrackingService;
import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;

//import bts.co.id.employeepresences.Services.xTrackServices;
//import bts.co.id.employeepresences.Services.xTrackerService;

public class EmployeePresencesApplication extends Application {

    private static Context mContext;
    private static DatabaseManager databaseManager;
    private static List<GeofenceModel> geofenceList;
    private static EmployeePresencesApplication instances;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    IntentFilter mIntentFilter;
    Messenger mService = null;
    boolean mIsBound;
    List<Workplace> workplaces;
    private GlobalManager globalManager;
    private List<String> geofencesIdsList;

    private GPSHelper gpsHelper;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            globalManager.writeTextFile("Battery Level = " + String.valueOf(level) + "%");
            if (isPhonePluggedIn(getApplicationContext()).compareToIgnoreCase("yes") == 0) {
                globalManager.writeTextFile("Charging:yes");
            } else {
                globalManager.writeTextFile("Charging:no");
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().toString().equals(StaticData.BroadCastUpdateAction)) {
                Log.v("getLocationUpdate Broadcast");
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null,
                        StaticData.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                Log.e("error connecting to service: " + e.getMessage());
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Log.i("disconnected from service :(");
        }
    };

    public static String isPhonePluggedIn(Context context) {
        boolean charging = false;
        String result = "No";
        final Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;

        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (batteryCharge)
            charging = true;
        if (usbCharge)
            charging = true;
        if (acCharge)
            charging = true;

        if (charging) {
            result = "Yes";
        }
        return result;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static boolean isDeviceRooted() {
        // Get the build tags info - See note below to know more about it
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        // Check if Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }
        // try executing commands as a superUser
        return canExecuteCommand("/system/xbin/which su") || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // Executes the specified string command in a separate process
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }
        return executedSuccesfully;
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public static EmployeePresencesApplication getInstances() {
        return instances;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void onCreate() {
        this.mContext = this;
        this.instances = this;
        this.globalManager = new GlobalManager(this);
        this.workplaces = new ArrayList<>();
        this.geofencesIdsList = new ArrayList<>();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(StaticData.BroadCastUpdateAction);
        registerReceiver(broadcastReceiver, mIntentFilter);

        isDeviceRooted();

        super.onCreate();
        setStrictPolicy();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
    }

    public void stopServices() {
        doUnbindService();
        this.stopService(new Intent(this.getApplicationContext(),
                TrackingService.class));
        if (getGlobalManager().getSharedPreferencesManager().isServicesRuning()) {
            getGlobalManager().getSharedPreferencesManager().setTrackingServices(false);
        }
    }

    public void startServices() {

        if (getGlobalManager().getSharedPreferencesManager().isServicesRuning()) {
            doUnbindService();
            this.stopService(new Intent(this.getApplicationContext(),
                    TrackingService.class));
        }
        this.startService(new Intent(this.getApplicationContext(),
                TrackingService.class));
        this.doBindService();
    }

    void doUnbindService() {
        if (!mIsBound)
            return;

        if (mService != null) {
            try {
                Message msg = Message.obtain(null,
                        StaticData.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
        }

        unbindService(mConnection);
        mIsBound = false;

        Log.i("service stopped");
    }

    void doBindService() {
        bindService(new Intent(this, TrackingService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (isConnected == true) {
            EmployeePresencesApplication app = (EmployeePresencesApplication) context.getApplicationContext();
//            app.startServices();
        } else {
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    public GlobalManager getGlobalManager() {
        if (this.globalManager == null) {
            this.globalManager = new GlobalManager(this);
        }
        return this.globalManager;
    }

    public void createGeofences(Location location) {
//        writeTextFile("Create geofence by "+location.getLatitude()+","+location.getLongitude());
//        this.workplaces  = globalManager.getSharedPreferencesManager().getWorkPlaces();
//
//        if (geofenceList == null){
//            this.geofenceList = new ArrayList<>();
//        }
//
//        if (geofencesIdsList == null){
//            this.geofencesIdsList = new ArrayList<>();
//        }
////        trackingService.updateNotification("workplaces size : "+ workplaces.size());
//        if (geofenceList.size() > 0 ){
//            geofenceList.clear();
//        }
//
//        if (geofencesIdsList.size() > 0){
//            geofencesIdsList.clear();
//        }
//
//
//        final List<Workplace> workPlacesIn  = new ArrayList<>();
//        float minDistance = 100;
//        for(int ix=0; ix < workplaces.size(); ix++) {
////            trackingService.updateNotification("createGeofences : "+ workplaces.get(ix).getWorkplaceName());
//
//            Workplace workplace = this.workplaces.get(ix);
//
//            float geodistance = distFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplace.getLatitude()), Float.parseFloat(workplace.getLongitude()));
//                if ( geodistance < 10000){
//                    writeTextFile("Create geofencing Workplaces "+workplace.getWorkplaceId()+" location "+workplace.getLatitude()+","+workplace.getLongitude()+" Distances = "+geodistance);
////                    writeTextFile("On Location Update "+location.getLatitude()+","+location.getLongitude());
//
//                    GeofenceModel geofenceModel = new GeofenceModel.Builder(workplace.getWorkplaceId())
//                        .setTransition(Geofence.GEOFENCE_TRANSITION_DWELL)
//                        .setLatitude(Double.parseDouble(workplace.getLatitude()))
//                        .setLongitude(Double.parseDouble(workplace.getLongitude()))
//                        .setRadius(100)
//                        .build();
//
//                    Log.i("andreTest"+workplace.getWorkplaceId()+" "+distFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplace.getLatitude()), Float.parseFloat(workplace.getLongitude())));
//
//                    geofencesIdsList.add(workplace.getWorkplaceId());
//                    geofenceList.add(geofenceModel);
//                }
//
//            if (distFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplace.getLatitude()), Float.parseFloat(workplace.getLongitude())) < minDistance){
////                writeTextFile("In Location geofencing Workplaces "+workplace.getWorkplaceId()+" location "+workplace.getLatitude()+","+workplace.getLongitude()+" Distances < "+minDistance);
//                Workplace workplace1 = workplace;
//                workPlacesIn.add(workplace1);
//                writeTextFile("Add Work Places In "+minDistance+" : "+workplace1.getWorkplaceId());
//            }
//        }
//
//        if (workPlacesIn.size() > 0){
//            int xy = 0;
//            do {
//                if (workPlacesIn.size() > xy){
//                    Workplace workplaceCheckIn = workPlacesIn.get(xy);
//                    minDistance = minDistance - 5;
//                    writeTextFile("Change distances min distances to = "+minDistance);
//                    if (distFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplaceCheckIn.getLatitude()), Float.parseFloat(workplaceCheckIn.getLongitude())) > minDistance){
//                        writeTextFile("Remove Workplaces = "+workPlacesIn.get(xy).getWorkplaceId()+" > "+minDistance);
//                        workPlacesIn.remove(xy);
//                        Log.i("WorkPlacesIn = "+workPlacesIn.size());
//                    }
//                    xy ++;
//                    if (xy == workPlacesIn.size() - 1){
//                        Log.i("Reset workplacesIn Counter");
//                        xy = 0;
//                    }
//                }
//            }while (workPlacesIn.size() > 1);
//            writeTextFile("Count of workplaces in user area is = "+workPlacesIn.size());
//            if (workPlacesIn.size() == 1){
//                writeTextFile("User in workplaces "+workPlacesIn.get(0).getWorkplaceId()+"; minDistance = "+minDistance);
//                writeTextFile("User Check in "+workPlacesIn.get(0).getWorkplaceId()+"; minDistance = "+minDistance);
//                globalManager.showNotification("Checkin in "+workPlacesIn.get(0).getWorkplaceName(),this);
//
//                String date = globalManager.getCurrentDateTime();
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date dateCheckIn = new Date();
//                try {
//                    dateCheckIn = df.parse(date);
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//                final Date finalDateCheckIn = dateCheckIn;
//                globalManager.getApiManager().user_checkin(workPlacesIn.get(0).getWorkplaceId(), date, new ApiListener() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
//                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
//                        writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server failure "+e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponseSuccess(String responseBody) throws IOException {
//                        RETURN_DATA return_data = new Gson().fromJson(responseBody,RETURN_DATA.class);
//                        Log.d(responseBody);
//                        getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),return_data.getData().getCheckinData().getId(), finalDateCheckIn, 1);
//                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),return_data.getData().getCheckinData().getId(), finalDateCheckIn, 1);
//                        writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server success ");
//                    }
//
//                    @Override
//                    public void onResponseError(String response) {
//                        getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
//                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
//                        writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server error "+response);
//                    }
//                });
//            }
//            Log.i("WorkPlacesIn = "+workPlacesIn.size());
//        }
    }

    public List<String> getGeofencesIdsList() {
        return geofencesIdsList;
    }

    public List<GeofenceModel> getGeofenceList(Location location) {
        createGeofences(location);
        return geofenceList;
    }

    private void setStrictPolicy() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

//    private void checkPermission() {

//        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(), this)) {
////            fetchLocationData();
//        }
//        else
//        {
//            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(), this);
//        }
//
//        if (!getGlobalManager().getMarshMallowPermission(this).checkPermissionForCamera()) {
//            getGlobalManager().getMarshMallowPermission(this).requestPermissionForCamera();
//        }
//
//        if (!getGlobalManager().getMarshMallowPermission(this).checkPermissionForExternalStorage()){
//            getGlobalManager().getMarshMallowPermission(this).requestPermissionForExternalStorage();
//        }
//
//        if (!getGlobalManager().getMarshMallowPermission(this).checkPermissionForRecord()){
//            getGlobalManager().getMarshMallowPermission(this).requestPermissionForRecord();
//        }
//    }

    public GPSHelper getGpsHelper() {
        if (this.gpsHelper == null) {
            gpsHelper = new GPSHelper(this.getApplicationContext());
        }
        return gpsHelper;
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StaticData.MSG_LOG:
                    Log.i(msg.getData().getString("log"));
                    break;

                case StaticData.MSG_LOG_RING:
                    ArrayList<ServicesLog> logs = (ArrayList) msg.obj;

                    for (int i = 0; i < logs.size(); i++) {
                        ServicesLog l = logs.get(i);
                        Log.i(l.message, l.date);
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    //    public GlobalManager getGlobalManager(){
//        if (globalManager == null){
//            globalManager = new GlobalManager(this);
//        }
//
//        return globalManager;
//    }

}
