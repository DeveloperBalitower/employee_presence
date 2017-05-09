package bts.co.id.employeepresences.Services.Controller;

import android.location.Location;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.Workplace;
import okhttp3.Request;

/**
 * Created by Andreas Panjaitan on 10/11/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class TrackServicesController {

    GlobalManager globalManager;
    List<Workplace> workplaces;

//    public TrackServicesController(xTrackServices services) {
//        this.services = services;
//        this.globalManager = new GlobalManager(this.services);
//    }

    public void onLocationChangeController(Location location) {
        if (globalManager.getSharedPreferencesManager().getIsCheckin()) {
            userCheckOut();
        } else {
            userCheckIn(location);
        }
    }

    private void userCheckOut() {
        final HistoryDataModel historyDataModel = globalManager.getDatabaseManager().getDataQuery(HistoryDataModel.class, "SELECT * FROM employee_precences_history order by id DESC LIMIT 0,1;");
        String[] dateString = globalManager.getCurrentDateTimeArray();
        historyDataModel.setCheck_out_date(dateString[1]);
        historyDataModel.setCheck_out_time(dateString[2]);
        historyDataModel.setUpload_status(0);
        if (historyDataModel.getServer_id() == null) {
            historyDataModel.setServer_id(0);
        }
        globalManager.getApiManager().user_checkOut(historyDataModel.getSite(), historyDataModel.getServer_id(), historyDataModel.getCheck_in_date() + " " + historyDataModel.getCheck_in_time(), dateString[0], new ApiListener() {
            @Override
            public void onFailure(Request request, IOException e) {
//                globalManager.getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
                globalManager.getDatabaseManager().checkOutUser(historyDataModel.getId(), historyDataModel.getServer_id(), historyDataModel.getSite(), historyDataModel.getCheck_out_date(), historyDataModel.getCheck_out_time(), 0);
                globalManager.getSharedPreferencesManager().checkOutPreferences();
            }

            @Override
            public void onResponseSuccess(String responseBody) throws IOException {
                globalManager.getDatabaseManager().checkOutUser(historyDataModel.getId(), historyDataModel.getServer_id(), historyDataModel.getSite(), historyDataModel.getCheck_out_date(), historyDataModel.getCheck_out_time(), 0);
                globalManager.getSharedPreferencesManager().checkOutPreferences();
            }

            @Override
            public void onResponseError(String response) {

            }
        });
    }

    private void userCheckIn(Location location) {
//        final Date date = getDateCheckIn();
//        this.workplaces  = services.getEmployeeApplication().getGlobalManager().getDatabaseManager().getWorkplaces();
//        final List<Workplace> workPlacesIn  = new ArrayList<>();
//        float minDistance = 100;
//        for(int ix=0; ix < workplaces.size(); ix++) {
////            trackingService.updateNotification("createGeofences : "+ workplaces.get(ix).getWorkplaceName());
//
//            Workplace workplace = this.workplaces.get(ix);
//
//            float geodistance = globalManager.distanceFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplace.getLatitude()), Float.parseFloat(workplace.getLongitude()));
//            if ( geodistance < 10000){
//                globalManager.writeTextFile("Create geofencing Workplaces "+workplace.getWorkplaceId()+" location "+workplace.getLatitude()+","+workplace.getLongitude()+" Distances = "+geodistance);
////                    writeTextFile("On Location Update "+location.getLatitude()+","+location.getLongitude());
//
//                GeofenceModel geofenceModel = new GeofenceModel.Builder(workplace.getWorkplaceId())
//                        .setTransition(Geofence.GEOFENCE_TRANSITION_DWELL)
//                        .setLatitude(Double.parseDouble(workplace.getLatitude()))
//                        .setLongitude(Double.parseDouble(workplace.getLongitude()))
//                        .setRadius(100)
//                        .build();
//
//                Log.i("andreTest"+workplace.getWorkplaceId()+" "+globalManager.distanceFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplace.getLatitude()), Float.parseFloat(workplace.getLongitude())));
//
////                geofencesIdsList.add(workplace.getWorkplaceId());
////                geofenceList.add(geofenceModel);
//            }
//
//            if (globalManager.distanceFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplace.getLatitude()), Float.parseFloat(workplace.getLongitude())) < minDistance){
////                writeTextFile("In Location geofencing Workplaces "+workplace.getWorkplaceId()+" location "+workplace.getLatitude()+","+workplace.getLongitude()+" Distances < "+minDistance);
//                Workplace workplace1 = workplace;
//                workPlacesIn.add(workplace1);
//                globalManager.writeTextFile("Add Work Places In "+minDistance+" : "+workplace1.getWorkplaceId());
//            }
//        }
//
//        if (workPlacesIn.size() > 0){
//            int xy = 0;
//            do {
//                if (workPlacesIn.size() > xy){
//                    Workplace workplaceCheckIn = workPlacesIn.get(xy);
//                    minDistance = minDistance - 5;
//                    globalManager.writeTextFile("Change distances min distances to = "+minDistance);
//                    if (globalManager.distanceFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(workplaceCheckIn.getLatitude()), Float.parseFloat(workplaceCheckIn.getLongitude())) > minDistance){
//                        globalManager.writeTextFile("Remove Workplaces = "+workPlacesIn.get(xy).getWorkplaceId()+" > "+minDistance);
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
//            globalManager.writeTextFile("Count of workplaces in user area is = "+workPlacesIn.size());
//            if (workPlacesIn.size() == 1){
//                globalManager. writeTextFile("User in workplaces "+workPlacesIn.get(0).getWorkplaceId()+"; minDistance = "+minDistance);
//                globalManager.writeTextFile("User Check in "+workPlacesIn.get(0).getWorkplaceId()+"; minDistance = "+minDistance);
//                globalManager.showNotification("Checkin in "+workPlacesIn.get(0).getWorkplaceName(),services.getApplicationContext());
//
//                final String date = globalManager.getCurrentDateTime();
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date dateCheckIn = new Date();
//                try {
//                    dateCheckIn = df.parse(date);
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//                final Date finalDateCheckIn = dateCheckIn;
//                final int[] lastRowId = {0};
//                globalManager.getApiManager().user_checkin(workPlacesIn.get(0).getWorkplaceId(), date, new ApiListener() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        lastRowId[0] = globalManager.getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
////                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
//                        setCheckInPresences();
////                        globalManager.writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server failure "+e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponseSuccess(String responseBody) throws IOException {
//                        RETURN_DATA return_data = new Gson().fromJson(responseBody,RETURN_DATA.class);
//                        Log.d(responseBody);
//                        lastRowId[0] = globalManager.getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),return_data.getData().getCheckinData().getId(), finalDateCheckIn, 1);
////                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),return_data.getData().getCheckinData().getId(), finalDateCheckIn, 1);
//                        setCheckInPresences(lastRowId[0]);
////                        globalManager.writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server success ");
//                    }
//
//                    @Override
//                    public void onResponseError(String response) {
//                        lastRowId[0] = globalManager.getDatabaseManager().checkINUser(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
//                        setCheckInPresences(lastRowId[0]);
////                        writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server error "+response);
//                    }
//                });
//            }
//            Log.i("WorkPlacesIn = "+workPlacesIn.size());
//        }
    }

    private Date getDateCheckIn() {
        String date = globalManager.getCurrentDateTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateCheckIn = new Date();
        try {
            dateCheckIn = df.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateCheckIn;
    }

    private void setCheckInPresences() {
        HistoryDataModel historyDataModel = globalManager.getDatabaseManager().getDataQuery(HistoryDataModel.class, "SELECT * FROM employee_precences_history order by id DESC LIMIT 0,1;");
        String historyDataModelJson = new Gson().toJson(historyDataModel, HistoryDataModel.class);
        globalManager.getSharedPreferencesManager().setHistoryDataModelPresences(historyDataModel);
        if (historyDataModel != null && historyDataModel.getCheck_in_date().length() > 0 && historyDataModel.getCheck_out_date() == null && historyDataModel.getCheck_in_time().length() > 0 && historyDataModel.getCheck_out_time() == null) {
            globalManager.getSharedPreferencesManager().setIsCheckIn(true);
        } else {
            globalManager.getSharedPreferencesManager().setIsCheckIn(false);
        }
    }
}
