package bts.co.id.employeepresences.Services.Controller;

import android.app.PendingIntent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.Workplace;
import bts.co.id.employeepresences.Services.TrackingService;

//import android.util.Log;

//import android.util.Log;

/**
 * Created by Andreas Panjaitan on 8/3/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class TrackingServiceController {

    private static String TAG = "TrackingService_LOG_Controller";
    private static List<Geofence> geofenceList;
    TrackingService trackingService;
    //    Workplace[] allWorkPlace;
//    private GlobalManager globalManager;
    List<Workplace> workplaces;
    private PendingIntent mGeofencePendingIntent;

    public TrackingServiceController(TrackingService trackingService) {
        this.trackingService = trackingService;
        workplaces = new ArrayList<>();
//        globalManager = trackingService.getGlobalManager();
//        createGeofences();
    }

    public static List<Geofence> getGeofenceList() {
        return geofenceList;
    }

    public static void setGeofenceList(List<Geofence> geofenceList) {
        TrackingServiceController.geofenceList = geofenceList;
    }

//    public void userCheckIn(final Workplace workPlaceLocation) {
//        final String date = trackingService.getGlobalManager().getCurrentDateTime();
//        Log.i("User CheckIn " + workPlaceLocation.getWorkplaceName() + " on " + date);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date dateCheckIn = new Date();
//        try {
//            dateCheckIn = df.parse(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        final Date finalDateCheckIn = dateCheckIn;
//        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
//        String historyDataJson = trackingService.getGlobalManager().getSharedPreferencesManager().getSharedPreferencesManager().getHistoryDataJSON();
//        if (trackingService.getGlobalManager().getSharedPreferencesManager().getIsCheckin()) {
//            if (!historyDataJson.isEmpty()) {
//                HistoryDataModel historyDataModel = new Gson().fromJson(historyDataJson, HistoryDataModel.class);
//                if (historyDataModel != null) {
//                    if (historyDataModel.getSite() == workPlaceLocation.getWorkplaceId()) {
//                        if (historyDataModel.getCheck_in_date() == dfDate.format(finalDateCheckIn)) {
//                            Log.i("User is allready checkin on workplace id " + workPlaceLocation.getWorkplaceId());
//                            return;
//                        }
//                    }
//                }
//                Log.i("checkInProcess " + workPlaceLocation.getWorkplaceName() + " " + date);
//                checkInProcess(workPlaceLocation, date);
//            }
//        } else {
//            Log.i("checkInProcess " + workPlaceLocation.getWorkplaceName() + " " + date);
//            checkInProcess(workPlaceLocation, date);
//        }
//        trackingService.updateNotification("You are in " + workPlaceLocation.getWorkplaceName());
//    }

//    private void checkInProcess(final Workplace workPlaceLocation, final String date) {
//        Log.i("Processing checkin on workplace id " + workPlaceLocation.getWorkplaceId());
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date dateCheckIn = new Date();
//        try {
//            dateCheckIn = df.parse(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        final Date finalDateCheckIn = dateCheckIn;
//        final long[] lastRowId = {0};
////        if (trackingService.getGlobalManager().getConnectionManager().checkConnectivity()){
//        trackingService.getGlobalManager().getApiManager().user_checkin(workPlaceLocation.getWorkplaceId(), date, new ApiListener() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                lastRowId[0] = trackingService.getGlobalManager().getDatabaseManager().checkINUser(workPlaceLocation.getWorkplaceId(), 0, finalDateCheckIn, 0);
////                    Log.i("checkin on workplace id "+workPlaceLocation.getWorkplaceId()+" failure > save to local db");
//                setCheckInPresences(lastRowId[0]);
//                Log.i("checkin on workplace id " + workPlaceLocation.getWorkplaceId() + " failure > save to local with id " + lastRowId[0]);
//            }
//
//            @Override
//            public void onResponseSuccess(String responseBody) throws IOException {
//                RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);
//                Log.i("checkin on workplace id " + workPlaceLocation.getWorkplaceId() + " Success > " + responseBody);
//                lastRowId[0] = trackingService.getGlobalManager().getDatabaseManager().checkINUser(workPlaceLocation.getWorkplaceId(), return_data.getData().getCheckinData().getId(), finalDateCheckIn, 1);
////                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),return_data.getData().getCheckinData().getId(), finalDateCheckIn, 1);
//                setCheckInPresences(lastRowId[0]);
//                Log.i("checkin on workplace id " + workPlaceLocation.getWorkplaceId() + " Success > save to local with id " + lastRowId[0]);
////                        globalManager.writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server success ");
//            }
//
//            @Override
//            public void onResponseError(String response) {
//                lastRowId[0] = trackingService.getGlobalManager().getDatabaseManager().checkINUser(workPlaceLocation.getWorkplaceId(), 0, finalDateCheckIn, 0);
//                setCheckInPresences(lastRowId[0]);
//                Log.i("checkin on workplace id " + workPlaceLocation.getWorkplaceId() + " Error > save to local with id " + lastRowId[0]);
////                        writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server error "+response);
//            }
//        });
////        }else{
////        lastRowId[0] = trackingService.getGlobalManager().getDatabaseManager().checkINUser(workPlaceLocation.getWorkplaceId(), 0, finalDateCheckIn, 0);
////        setCheckInPresences(lastRowId[0]);
////        }
//
//        trackingService.getGlobalManager().writeTextFile("User CheckIn on " + workPlaceLocation.getWorkplaceName());
//    }

//    public void setWorkplaces() {
//
//    }

    private void setCheckInPresences(long rowId) {
        HistoryDataModel historyDataModel = trackingService.getGlobalManager().getDatabaseManager().getDataQuery(HistoryDataModel.class, "SELECT * FROM employee_precences_history where id = '" + rowId + "';");
        trackingService.getGlobalManager().getSharedPreferencesManager().setHistoryDataModelPresences(historyDataModel);
        if (historyDataModel != null && historyDataModel.getCheck_in_date().length() > 0 && historyDataModel.getCheck_out_date() == null && historyDataModel.getCheck_in_time().length() > 0 && historyDataModel.getCheck_out_time() == null) {
            trackingService.getGlobalManager().getSharedPreferencesManager().setIsCheckIn(true);
        } else {
            trackingService.getGlobalManager().getSharedPreferencesManager().setIsCheckIn(false);
        }
    }

    public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

//    public boolean checkGooglePlayServices() {
//        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (result == ConnectionResult.SUCCESS) {
//            return true;
//        }
//        else {
//            Dialog errDialog = GooglePlayServicesUtil.getErrorDialog(
//                    result,
//                    this,
//                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
//
//            if (errorDialog != null) {
//                errorDialog.show();
//            }
//        }
//        return false;
//    }

    public void createGeofences() {
//        this.workplaces  = this.trackingService.getEmployApplication().getGlobalManager().getDatabaseManager().getWorkplaces();
//        this.geofenceList = new ArrayList<>();
//        trackingService.updateNotification("workplaces size : "+ workplaces.size());
//        for(int ix=0; ix < workplaces.size(); ix++) {
//            trackingService.updateNotification("createGeofences : "+ workplaces.get(ix).getWorkplaceName());
//            Geofence fence = new Geofence.Builder()
//                    .setRequestId(workplaces.get(ix).getWorkplaceId())
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                            Geofence.GEOFENCE_TRANSITION_EXIT)
//                    .setCircularRegion(
//                            Double.parseDouble(workplaces.get(ix).getLatitude()), Double.parseDouble(workplaces.get(ix).getLongitude()), StaticData.CHECK_IN_RANGE)
//                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                    .build();
//            geofenceList.add(fence);
//        }
    }

    public List<Workplace> getWorkplaces() {
        return workplaces;
    }

    public void setWorkplaces(List<Workplace> workplaces) {
        this.workplaces = workplaces;
    }

//    public PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (mGeofencePendingIntent != null) {
//            return mGeofencePendingIntent;
//        }
//        Intent intent = new Intent(this.trackingService, xGeofenceTransitionsIntentService.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//        // calling addGeofences() and removeGeofences().
//        return PendingIntent.getService(this.trackingService, 0, intent, PendingIntent.
//                FLAG_UPDATE_CURRENT);
//    }

    private static class WorkPlaceLocation {
        public LatLng mLatLng;
        public String mId;
        public String mName;
        public String mAddress;

        WorkPlaceLocation(LatLng latlng, String id, String name, String address) {
            mLatLng = latlng;
            mId = id;
            mName = name;
            mAddress = address;
        }
    }
}
