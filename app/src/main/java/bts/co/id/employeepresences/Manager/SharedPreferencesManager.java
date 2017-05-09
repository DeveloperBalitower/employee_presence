package bts.co.id.employeepresences.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.List;

import bts.co.id.employeepresences.Model.Auth;
import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.Site;
import bts.co.id.employeepresences.Model.SiteList;
import bts.co.id.employeepresences.Model.StaticData;
import bts.co.id.employeepresences.Model.User;
import bts.co.id.employeepresences.Model.WorkPlaces;
import bts.co.id.employeepresences.Model.Workplace;

/**
 * Created by IT on 7/14/2016.
 */
public class SharedPreferencesManager extends GlobalManager {

    SharedPreferences preferences;

    public SharedPreferencesManager(Context context) {
        super(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public boolean isLogin() {
        return preferences.getBoolean(StaticData.PREF_IS_LOGIN, false);
//        Log.e("loginResult",preferences.getString(context.getString(R.string.pref_dataLoginReturn),""));
    }

    public boolean getIsLogin() {
        return preferences.getBoolean(StaticData.PREF_IS_LOGIN, false);
    }

    public void setIsLogin(boolean isLogin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(StaticData.PREF_IS_LOGIN, isLogin);
        editor.commit();
    }

    public void setCheckInDate(){

    }

    public void setIsCheckIn(boolean isCheckin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(StaticData.PREF_IS_CHECKIN, isCheckin);
        editor.commit();
    }

    public void setUserLoginPref(User userLogin) {
        Gson gson = new Gson();
        String userLoginResult = gson.toJson(userLogin);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_USER, userLoginResult);
        editor.commit();
    }

    public void setUserLoginPref(String userLogin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_USER, userLogin);
        editor.commit();
    }

    public void setUserFirstInstall(boolean isFirst) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(StaticData.PREF_IS_FIRST, isFirst);
        editor.commit();
    }

    public boolean isFirstInstall() {
        return preferences.getBoolean(StaticData.PREF_IS_FIRST, false);
    }

    public User getUserLogin() {
        Gson gson = new Gson();
        User loginReturn = gson.fromJson(preferences.getString(StaticData.PREF_USER, ""), User.class);
        return loginReturn;
    }

    public boolean getIsCheckin() {
        return preferences.getBoolean(StaticData.PREF_IS_CHECKIN, false);
    }

//    public String getHistoryDataModelString(){
//        return preferences.getString(StaticData.PREF_HISTORY_DATA,"");
//    }

    public HistoryDataModel getHistoryDataModel() {
        return new Gson().fromJson(preferences.getString(StaticData.PREF_HISTORY_DATA, ""), HistoryDataModel.class);
    }

    public void setHistoryDataModelPresences(String historyDataModel) {
        Log.i("setHistoryDataModelPresences = " + historyDataModel);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_HISTORY_DATA, historyDataModel);
        editor.commit();
    }

    public void checkOutPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(StaticData.PREF_IS_CHECKIN);
        editor.remove(StaticData.PREF_HISTORY_DATA);
        editor.commit();
    }

    public void setHistoryDataModelPresences(HistoryDataModel historyDataModel) {
        String json = new Gson().toJson(historyDataModel, HistoryDataModel.class);
        Log.i("setHistoryDataModelPresences = " + json);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_HISTORY_DATA, json);
        editor.commit();
    }

    public String getHistoryDataJSON() {
        return preferences.getString(StaticData.PREF_HISTORY_DATA, "");
    }

    public String getStringUserLogin() {
        return preferences.getString(StaticData.PREF_USER, "");
    }

    public Auth getUserAuth() {
        Gson gson = new Gson();
//        if (preferences.getString(StaticData.PREF_AUTH, "").equals("")) {
//            return null;
//        }
        String StringJosin = preferences.getString(StaticData.PREF_AUTH, "");
        Auth userAuth = gson.fromJson(StringJosin, Auth.class);
        return userAuth;
    }

    public void setUserAuth(Auth auth) {
        Gson gson = new Gson();
        String authString = gson.toJson(auth);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_AUTH, authString);
        editor.commit();
        String StringJosin = preferences.getString(StaticData.PREF_AUTH, "");
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setTrackingServices(boolean isRunning) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(StaticData.PREF_IS_SERVICES_RUNING, isRunning);
        editor.commit();
    }

    public List<Site> getSiteList() {
        SiteList loginReturn = new Gson().fromJson(preferences.getString(StaticData.PREF_SITELIST, ""), SiteList.class);
        return loginReturn.getSites();
    }

    public void setSiteList(SiteList siteList) {
        String siteListJson = new Gson().toJson(siteList, SiteList.class);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_SITELIST, siteListJson);
        editor.commit();
    }

    public List<Workplace> getWorkPlaces() {
        WorkPlaces loginReturn = new Gson().fromJson(preferences.getString(StaticData.PREF_WORKPLACES, ""), WorkPlaces.class);
        return loginReturn.getWorkplaces();
    }

    public void setWorkPlaces(WorkPlaces workPlaces) {
        String workPlaceList = new Gson().toJson(workPlaces, WorkPlaces.class);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(StaticData.PREF_WORKPLACES, workPlaceList);
        editor.commit();
    }

    public boolean isServicesRuning() {
        return preferences.getBoolean(StaticData.PREF_IS_SERVICES_RUNING, false);
    }

    public String getEndpoint() {
        return preferences.getString(StaticData.ENDPOINT, "");
    }

    public String getUpdateFreq() {
        return preferences.getString(StaticData.UPDATE_FREQ, "30m");
    }

    public boolean trackNetwork() {
        return preferences.getBoolean(StaticData.PREF_NETWORK_PREF, true);
    }

    public boolean trackGPS() {
        return preferences.getBoolean(StaticData.PREF_GPS_PREF, true);
    }

    public boolean doDebugLogging() {
        return preferences.getBoolean(StaticData.PREF_DEBUG_PREF, false);
    }

    public boolean trackSignalStrength() {
        return preferences.getBoolean(StaticData.PREF_SIGNAL_PREF, false);
    }

    public float getLocationMinDistance() {
        try {
            String disString = preferences.getString(StaticData.PREF_MIN_DIS_PREF, "1");
            return Float.parseFloat(disString);
        } catch (NumberFormatException e) {
            Log.e("Invalid preference for location min distance", e);
        }
        return 0;
    }

    public long getLocationUpdateTime() {
        try {
            String timeString = preferences.getString(StaticData.PREF_MIN_TIME_PREF, "1");
            long secondsTime = Long.valueOf(timeString);
//            return secondsTime;
            return secondsTime * 1000;
        } catch (NumberFormatException e) {
            Log.e("Invalid preference for location min time" + e);
        }
        return 0;
    }


}
