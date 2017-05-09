package bts.co.id.employeepresences.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bts.co.id.employeepresences.Listener.CheckOutReceiverListener;
import bts.co.id.employeepresences.Manager.Log;

/**
 * Created by Andreas Panjaitan on 1/9/2017.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class CheckOutReceiver extends BroadcastReceiver {
    private CheckOutReceiverListener checkOutReceiverListener;

    public CheckOutReceiver() {

    }

    public CheckOutReceiver(CheckOutReceiverListener checkOutReceiverListener) {
        this.checkOutReceiverListener = checkOutReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("checkOutReceiverListener", "tes");
        checkOutReceiverListener.isCheckOut(true);
    }
}