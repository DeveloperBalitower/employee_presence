package bts.co.id.employeepresences.Model;

import android.content.Context;


/**
 * Created by IT on 7/14/2016.
 */
public class StaticData {
    // preference constants
    public static final String PREF_MIN_TIME_PREF = "mintime_preference";
    public static final String PREF_MIN_DIS_PREF = "mindistance_preference";
    public static final String PREF_GPS_PREF = "gps_preference";
    public static final String PREF_NETWORK_PREF = "network_preference";
    public static final String PREF_SIGNAL_PREF = "signal_preference";
    public static final String PREF_DEBUG_PREF = "advanced_log_preference";
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_LOG = 3;
    public static final int MSG_LOG_RING = 4;
    public static final String ACTION_GEOFENCE_ERROR = "ACTION_GEOFENCE_ERROR";
    public static final String ACTION_GEOFENCES_ADDED = "ACTION_GEOFENCES_ADDED";
    public static final String ACTION_GEOFENCES_REMOVED = "ACTION_GEOFENCES_REMOVED";
    public static final String ACTION_GEOFENCE_TRANSITION = "ACTION_GEOFENCE_TRANSITION";
    //    public static String BASE_URL = "http://bts_user_presence.dev/";
    public static Context applicationContext = null;
    public static String DB_INIT_SCRIPT_FILENAME = "initdb.sql";
    public static boolean isDebuggable = true;
    public static String PROCESS_NAME = "";
    public static String PREF_IS_LOGIN = "IS_LOGIN";
    public static String PREF_IS_FIRST = "IS_FIRST";
    public static String PREF_USER = "USER_LOGIN";
    public static String PREF_IS_SERVICES_RUNING = "IS_SERVICES_RUNING";
    public static String PREF_AUTH = "AUTH";
    public static String PREF_SITELIST = "PREF_SITELIST";
    public static String PREF_WORKPLACES = "PREF_WORKPLACES";
    public static String PREF_HISTORY_DATA = "PREF_HISTORY_DATA";
    public static String OnUpdateLocationBroadCast = "OnLocationUpdate";
    public static String BroadCastUpdateAction = "bts.co.id.employeepresences.Services.LocationService.updatelocation";
    //    public static float CHECK_IN_RANGE = 100;
    public static String ENDPOINT = "endpoint";
    public static String ENABLED = "enabled";
    public static String UPDATE_FREQ = "update_freq";
    public static String LAST_POST_TIME = "last_post_time";
    public static String PREF_IS_CHECKIN = "PREF_IS_CHECKIN";
    public static double DISTANCES = 100.0;


//    public static boolean isMyServiceRunning(Context mContext) {
//        ActivityManager manager = (ActivityManager) mContext
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager
//                .getRunningServices(Integer.MAX_VALUE)) {
//            if (GPSTracker.class.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

}
