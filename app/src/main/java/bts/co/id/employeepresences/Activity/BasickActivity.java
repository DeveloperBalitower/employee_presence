package bts.co.id.employeepresences.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.assist.AssistContent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import bts.co.id.employeepresences.EmployeePresencesApplication;
import bts.co.id.employeepresences.Manager.DatabaseManager;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.StaticData;

public class BasickActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    public static EmployeePresencesApplication application;
    public boolean doubleBackToExitPressedOnce = false;
    private GlobalManager globalManager;
//    private DatabaseManager databaseManager;

    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    public static EmployeePresencesApplication getEmpApplication() {
        return application;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("OnCreate");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        application = (EmployeePresencesApplication) EmployeePresencesApplication.getmContext();
        StaticData.applicationContext = getApplicationContext();
        StaticData.isDebuggable = (this.getApplicationInfo().flags & this.getApplicationInfo().FLAG_DEBUGGABLE) != 0;
        StaticData.PROCESS_NAME = this.getApplicationInfo().processName;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initDbManager();
        super.onCreate(savedInstanceState);
        checkPermission();
        getGlobalManager().checkGPS();
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getApplicationContext(), this);
        }
        else {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext(), this)) {
            }
            else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getApplicationContext(), this);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
            }
        }
    }

    public void removeActionBar() {
        this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        this.getSupportActionBar().hide();
    }

    public void fullScreen() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void initDbManager() {
        if (!DatabaseManager.getInstance().isDatabaseExists()) {
            DatabaseManager.getInstance().createDatabase(this.getApplicationContext());
            DatabaseManager.getInstance().openDatabase();
            DatabaseManager.getInstance().initDatabase();
        }
    }

    @Override
    public void onProvideAssistContent(AssistContent outContent) {
        //
    }

    public GlobalManager getGlobalManager() {
        if (this.globalManager == null) {
            this.globalManager = new GlobalManager(this);
        }
        return globalManager;
    }

    public void finishApplication() {
//        this.finishAffinity();
        System.exit(0);
    }

    public void setDoubleBackToExitPressedOnce(boolean bool) {
        this.doubleBackToExitPressedOnce = bool;
    }

    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    fetchLocationData();

                } else {

                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGlobalManager().checkGPS();
    }
}