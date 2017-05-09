package bts.co.id.employeepresences.Activity.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;

/**
 * Created by Andreas Panjaitan on 11/16/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class GPSDialogActivity extends Activity {

    GPSDialogActivity gpsDialogActivity;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("bts.co.id.employeepresences.ShowNotification")) {
                GlobalManager globalManager = new GlobalManager(gpsDialogActivity);
                globalManager.checkGPS();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gpsDialogActivity = this;
        Log.i("GPSDialogActivity", "On Create");
        registerReceiver(broadcastReceiver, new IntentFilter(""));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
