package bts.co.id.employeepresences.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andreas Panjaitan on 11/7/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class ConnectionManager extends GlobalManager {
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public ConnectionManager(Context context) {
        super(context);
    }

    /**
     * Constructor
     */

    public ConnectivityManager getConnectivityManager() {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return connectivityManager;
    }

    public NetworkInfo getNetworkInfo() {
        if (networkInfo == null) {
            NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        }
        return networkInfo;
    }

    public boolean checkConnectivityX() {
//        if (getNetworkInfo() != null && getNetworkInfo().isConnected()) {
//            isAvailable = true;
//        }
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL(ApiManager.BASE_URL + "test").openConnection());
            urlc.setRequestProperty("User-Agent", "EmployeeApplication");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(3000); //choose your own timeframe
            urlc.setReadTimeout(4000); //choose your own timeframe
            urlc.connect();
            urlc.getResponseCode();
            Log.e("ConnectionManager Response Code " + urlc.getResponseCode());
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e("ConnectionManager IOException : " + e.getMessage());
            return (false);  //connectivity exists, but no internet.
        }
    }
}
