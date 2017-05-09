package bts.co.id.employeepresences.Manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bts.co.id.employeepresences.Activity.View.HomeActivity;
import bts.co.id.employeepresences.Activity.View.MainActivity;
import bts.co.id.employeepresences.R;

import static bts.co.id.employeepresences.Manager.DatabaseManager.DATABASE_NAME;

/**
 * Created by IT on 7/14/2016.
 */
public class GlobalManager {
    public Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UIManager uiManager;
    private NotificationManager notificationManager;
    private Notification notification;
    private Notification.Builder notifBuilder;
    private DatabaseManager databaseManager;
    private MarshMallowPermission marshMallowPermission;
    private ConnectionManager connectionManager;

    public GlobalManager(Context context) {
        this.context = context;
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean areThereMockPermissionApps(Context context) {

        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " + e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    public SharedPreferencesManager getSharedPreferencesManager() {
        if (this.sharedPreferencesManager == null) {
            this.sharedPreferencesManager = new SharedPreferencesManager(this.context);
        }
        return this.sharedPreferencesManager;
    }

    public UserManager getUserManager() {
        if (this.userManager == null) {
            this.userManager = new UserManager(this.context);
        }

        return this.userManager;
    }

    public UIManager getUiManager() {
        if (this.uiManager == null) {
            this.uiManager = new UIManager(this.context);
        }
        return uiManager;
    }

    public String getDevicesID() {
        return Settings.Secure.getString(this.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public String[] getCurrentDateTimeArray() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dfDate.format(c.getTime());
        String time = dfTime.format(c.getTime());
        String fulldate = df.format(c.getTime());
        String[] stringDate = {fulldate, date, time};
        return stringDate;
    }

    public String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
        return df.format(c.getTime());
    }

    public Date getDateCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    public int getDpToInt(int sizeInDp) {
        float scale = this.context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (sizeInDp * scale + 0.5f);
        return dpAsPixels;
    }

    public ApiManager getApiManager() {
        if (apiManager == null) {
            apiManager = new ApiManager(context);
        }
        return apiManager;
    }

    public Notification showNotification(String str, Context context) {

//        Notification.Builder notificationBuilder = getNotifBuilder(context);
//        notificationBuilder.setContentText(str);
//
//        notification = notificationBuilder.getNotification();
//        notificationManager.notify(11, notification);
        int ID = 1234;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(context.getApplicationContext());
        builder.setContentIntent(pendIntent);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker(str);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(false);
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        builder.setContentText(str);

        Notification notification = builder.build();

//        startForeground(ID, notification);
        return notification;

    }

    public NotificationManager getNotificationManager(Context context) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private Notification.Builder getNotifBuilder(Context context) {
        if (notifBuilder == null) {
            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE); // getNotificationManager(context);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, HomeActivity.class), 0);
            notifBuilder = new Notification.Builder(context.getApplicationContext());
            notifBuilder.setAutoCancel(false);
            notifBuilder.setTicker("this is ticker text");
            notifBuilder.setContentTitle(context.getString(R.string.app_name));
            notifBuilder.setSmallIcon(R.drawable.ic_launcher);
            notifBuilder.setContentIntent(contentIntent);
            notifBuilder.setOngoing(true);
        }
        return notifBuilder;
    }

    public void checkGPS() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gps_enabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        context.getApplicationContext().startActivity(myIntent);
                        context.startActivity(myIntent);
                        //get gps
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.Cancel).toUpperCase(), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
        } catch (Exception ex) {
        }
    }

    public boolean checkMockLocation() {
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else return true;
    }

    public boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL(ApiManager.BASE_URL + "test");   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(10 * 1000);          // 10 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.wtf("Connection", "Success !");
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public float distanceFrom(float lat1, float lng1, float lat2, float lng2) {
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

    public void writeTextFile(String text) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
            SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm:ss");
            Date date = new Date();
            File root = new File(Environment.getExternalStorageDirectory(), "EmployeePresences/");
            if (!root.exists()) {
                android.util.Log.e("WriteText", "Root Not Exists");
                if (!root.mkdir()) {
                    android.util.Log.e("WriteText", "Failed to create direcktory");
                    return;
                }
            }

            File fileLog = new File(root, "log-" + format.format(date) + ".txt");
            if (!fileLog.exists()) {
                android.util.Log.e("WriteText", "Can't found file Log");
                if (!fileLog.createNewFile()) {
                    android.util.Log.e("WriteText", "Can't create file Log");
                    return;
                }
            }

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(fileLog, true));
            final String currentDateTimeString = timeformat.format(new Date());
            buf.append(currentDateTimeString + " : " + text);
            buf.newLine();
            buf.close();

//            writer.append(text+"\n\n");
//            writer.flush();
//            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.util.Log.i("WriteText", "write : " + text);
    }

    public DatabaseManager getDatabaseManager() {
        if (this.databaseManager == null) {
            this.databaseManager = DatabaseManager.getInstance();
        }
        return this.databaseManager;
    }

    public MarshMallowPermission getMarshMallowPermission(Activity activity) {
        if (marshMallowPermission == null) {
            this.marshMallowPermission = new MarshMallowPermission(activity);
        }
        return marshMallowPermission;
    }

    public String getVersionNumber() {
        String version = "0.0";
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public boolean isDatabaseExists() {
        try {
            File fileDb = new File(Environment.getExternalStorageDirectory(), "EmployeePresences/" + DATABASE_NAME);
            if (fileDb.exists()) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public ConnectionManager getConnectionManager() {
        if (this.connectionManager == null) {
            this.connectionManager = new ConnectionManager(context);
        }
        return this.connectionManager;
    }

    public double getDistancesPositionOnMeters(double userLat, double userLng, double venueLat, double venueLng) {
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Log.i("c " + c);
        double dist = 6371 * c;
        Log.i("dist " + dist);
        double distMeters = dist * 100;
        Log.i("distMeters " + distMeters);
        Locale currentLocale = context.getResources().getConfiguration().locale;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat twoDForm = new DecimalFormat("#.##", otherSymbols);
        Log.i("twoDForm " + twoDForm.format(distMeters));
        return Double.valueOf(twoDForm.format(distMeters));
    }
}
