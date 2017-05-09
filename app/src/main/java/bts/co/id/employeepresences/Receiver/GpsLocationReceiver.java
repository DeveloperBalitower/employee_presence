package bts.co.id.employeepresences.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bts.co.id.employeepresences.EmployeePresencesApplication;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;


/**
 * Created by IT on 7/21/2016.
 */
public class GpsLocationReceiver extends BroadcastReceiver {

    protected EmployeePresencesApplication application;
    private GlobalManager globalManager;
    private Intent locationServicesIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("On Receive Broadcast");
        Intent intentX = new Intent();
        intentX.setAction("bts.co.id.employeepresences.ShowNotification");
        context.sendBroadcast(intentX);
//        globalManager = new GlobalManager(context);
//
//        globalManager.checkGPS();
    }
}