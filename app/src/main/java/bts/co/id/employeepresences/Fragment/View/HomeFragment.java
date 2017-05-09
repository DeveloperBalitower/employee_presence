package bts.co.id.employeepresences.Fragment.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bts.co.id.employeepresences.BroadcastReceiver.CheckOutReceiver;
import bts.co.id.employeepresences.Fragment.BasickFragment;
import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Listener.CheckOutReceiverListener;
import bts.co.id.employeepresences.Manager.DatabaseManager;
import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import bts.co.id.employeepresences.Model.StaticData;
import bts.co.id.employeepresences.Model.Workplace;
import bts.co.id.employeepresences.R;
import okhttp3.Request;

public class HomeFragment extends BasickFragment {

    android.app.AlertDialog dialog;
    CheckOutReceiver checkOutReceiver;
    private View view;
    private Button checkin_button;
    private TextView tv_checkin_site, tv_checkin_date, tv_checkout_date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);
        initUI();
        setupUI();
        checkOutReceiver = new CheckOutReceiver(new CheckOutReceiverListener() {
            @Override
            public void isCheckOut(Boolean isCheckout) {
                initUI();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
        setupUI();
    }

    private void setupUI() {
        if (this.getGlobalManager().getSharedPreferencesManager().getIsCheckin()) {
            this.checkin_button.setText(getMainActivity().getResources().getString(R.string.checkout));
        } else {
            this.checkin_button.setText(getMainActivity().getResources().getString(R.string.checkin));
        }
        if (getGlobalManager().getSharedPreferencesManager().getHistoryDataModel() != null && !getGlobalManager().getSharedPreferencesManager().getHistoryDataJSON().isEmpty()) {
            HistoryDataModel historyDataModel = getGlobalManager().getSharedPreferencesManager().getHistoryDataModel();

            String checkINName = "";
            String checkOut =" - ";
            String checkIn = " - ";

            if (historyDataModel.getLocationName() != null && !historyDataModel.getLocationName().equals("null") && !historyDataModel.getLocationName().isEmpty()){
                checkINName = historyDataModel.getLocationName();
            }else{
                checkINName = historyDataModel.getSite();
            }

            if (historyDataModel.getCheck_out_date() != null && !historyDataModel.getLocationName().equals("null") && !historyDataModel.getCheck_out_date().isEmpty()){
                checkOut = historyDataModel.getCheck_out_date();
            }

            if (historyDataModel.getCheck_out_time() != null && !historyDataModel.getCheck_out_time().isEmpty()){
                checkOut += checkIn += " "+historyDataModel.getCheck_out_time();
            }

            if (historyDataModel.getCheck_in_date() != null &&  !historyDataModel.getCheck_in_date().isEmpty()){
                checkIn = historyDataModel.getCheck_in_date();
            }

            if (historyDataModel.getCheck_in_time() != null && !historyDataModel.getCheck_in_time().isEmpty()){
                checkIn += " "+historyDataModel.getCheck_in_time();
            }

            this.tv_checkin_site.setVisibility(View.VISIBLE);
            this.tv_checkin_date.setVisibility(View.VISIBLE);
            this.tv_checkout_date.setVisibility(View.VISIBLE);

            this.tv_checkout_date.setText(checkOut);
            this.tv_checkin_site.setText(checkINName);
            this.tv_checkin_date.setText(checkIn);
        }else{
            this.tv_checkin_site.setVisibility(View.GONE);
            this.tv_checkin_date.setVisibility(View.GONE);
            this.tv_checkout_date.setVisibility(View.GONE);
        }


        checkin_button.setEnabled(true);
        checkin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkin_button.getText().equals(getActivity().getString(R.string.checkin))) {
                    getMainActivity().getEmpApplication().startServices();
                    checkinProcess();
                } else {
                    checkOutProcess();
                }
            }
        });
    }

    private void checkOutProcess() {
        Log.d("yusa","uisa");
        final Double lat = getMainActivity().getEmpApplication().getGpsHelper().getLatitude();
        final Double lng = getMainActivity().getEmpApplication().getGpsHelper().getLongitude();
        final HistoryDataModel historyDataModel = getGlobalManager().getSharedPreferencesManager().getHistoryDataModel();
        if (historyDataModel.getSite() != null
                && !historyDataModel.getSite().isEmpty()
                && historyDataModel != null
                && historyDataModel.getCheck_in_date() != null
                && !historyDataModel.getCheck_in_date().isEmpty()
                && historyDataModel.getCheck_in_time() != null
                && !historyDataModel.getCheck_in_time().isEmpty()
                && historyDataModel.getCheckin_lat() != null
                && historyDataModel.getCheckin_long() != null) {
            dialog = getGlobalManager().getUiManager().getSpotsDialog(getActivity());
            dialog.show();
            float[] resultsDistanceByLocation = new float[1];
            Workplace workplace = DatabaseManager.getInstance().getWorkPlace(historyDataModel.getSite());
            Location.distanceBetween(Double.valueOf(workplace.getLatitude()), Double.valueOf(workplace.getLongitude()),
                    lat, lng, resultsDistanceByLocation);
            Log.d("DistanceByLocation", String.valueOf(resultsDistanceByLocation[0]));
            Log.d("StatisDistance", String.valueOf(StaticData.DISTANCES));
            if (resultsDistanceByLocation[0] > StaticData.DISTANCES) {
                final Date dateCheckOutDaate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd:mm:ss");
                historyDataModel.setCheck_out_date(dateFormat.format(dateCheckOutDaate));
                historyDataModel.setCheck_out_time(timeFormat.format(dateCheckOutDaate));
                historyDataModel.setCheckout_lat(lat);
                historyDataModel.setCheckout_long(lng);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date dateCheckOut = new Date();

                String dateCheckIn = historyDataModel.getCheck_in_date() + " " + historyDataModel.getCheck_in_time();
                //String dateCheckOut = historyDataModel.getCheck_out_date() + " " + historyDataModel.getCheck_out_time();
//                PresencesHistory history;
                final List<HistoryDataModel> historyDataModelList = new ArrayList<>();
                getGlobalManager().getApiManager().user_checkOut(historyDataModel.getSite(), historyDataModel.getServer_id(), dateCheckIn, df.format(dateCheckOut), new ApiListener() {
                    @Override
                    public void onFailure(Request request, IOException e) {
//                        getApplicationContext()
                        getMainActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                historyDataModelList.add(historyDataModel);
                                DatabaseManager.getInstance().insertPresencesHistory(historyDataModelList, 0);
                                getGlobalManager().getSharedPreferencesManager().checkOutPreferences();
                                checkin_button.setText(getActivity().getString(R.string.checkin));
                                Intent intent = new Intent();
                                intent.setAction("bts.co.id.employeepresencest.UserCheckOutBroadCast");
                                getMainActivity().sendBroadcast(intent);
                                setupUI();
                            }
                        });
                    }

                    @Override
                    public void onResponseSuccess(String responseBody) throws IOException {
                        getMainActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                historyDataModelList.add(historyDataModel);
                                DatabaseManager.getInstance().insertPresencesHistory(historyDataModelList, 1);
                                getGlobalManager().getSharedPreferencesManager().checkOutPreferences();
                                checkin_button.setText(getActivity().getString(R.string.checkin));
                                Intent intent = new Intent();
                                intent.setAction("bts.co.id.employeepresencest.UserCheckOutBroadCast");
                                //getMainActivity().sendBroadcast(intent);
                                dialog.dismiss();
                                setupUI();
                            }
                        });

                    }

                    @Override
                    public void onResponseError(String response) {
                        getMainActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Log.d("Checkout Employee", "Error");
                                historyDataModelList.add(historyDataModel);
                                DatabaseManager.getInstance().insertPresencesHistory(historyDataModelList, 0);
                                getGlobalManager().getSharedPreferencesManager().checkOutPreferences();
                                checkin_button.setText(getActivity().getString(R.string.checkin));
                                Intent intent = new Intent();
                                intent.setAction("bts.co.id.employeepresencest.UserCheckOutBroadCast");
                                //getMainActivity().sendBroadcast(intent);
                                setupUI();
                            }
                        });
                    }
                });
            } else {
                dialog.dismiss();
                Toast.makeText(getMainActivity(), "Anda tidak bisa checkout jika jarak terhadap site tidak lebih besar dari " + StaticData.DISTANCES + " meter", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkinProcess() {
        dialog = getGlobalManager().getUiManager().getSpotsDialog(getActivity());
        dialog.show();
        getMainActivity().getEmpApplication().getGpsHelper().getMyLocation();
        final Double lat = getMainActivity().getEmpApplication().getGpsHelper().getLatitude();
        final Double lng = getMainActivity().getEmpApplication().getGpsHelper().getLongitude();
        final List<Workplace> workplaces = getGlobalManager().getDatabaseManager().getWorkplaces(lat, lng, StaticData.DISTANCES);
        Workplace workplace = new Workplace();
        final List<String> items = new ArrayList<String>();
        for (int i = 0; i < workplaces.size(); i++) {
            items.add(workplaces.get(i).getWorkplaceName());
            workplace = workplaces.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Check In To?");
        final Workplace finalWorkplace = workplace;
        final CharSequence[] listItem = items.toArray(new String[items.size()]);
        builder.setItems(listItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog = getGlobalManager().getUiManager().getSpotsDialog(getActivity());
                dialog.show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date dateCheckIn = new Date();
                final String finalDateCheckIn = df.format(dateCheckIn);
                final long[] lastRowId = {0};
                getGlobalManager().getApiManager().user_checkin(finalWorkplace.getWorkplaceId(), df.format(dateCheckIn), lat, lng, new ApiListener() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                lastRowId[0] = getGlobalManager().getDatabaseManager().checkINUser(finalWorkplace.getWorkplaceId(), 0, lat, lng, dateCheckIn, 0);
//                        setCheckInPresences(workPlacesIn.get(0).getWorkplaceId(),0, finalDateCheckIn, 0);
                                setCheckInPresences(lastRowId[0]);
//                        globalManager.writeTextFile(workPlacesIn.get(0).getWorkplaceName() + "Checkin to server failure "+e.getMessage());
                                setupUI();
                            }
                        });
                    }

                    @Override
                    public void onResponseSuccess(final String responseBody) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                    RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);//
                                    lastRowId[0] = getGlobalManager().getDatabaseManager().checkINUser(finalWorkplace.getWorkplaceId(), return_data.getData().getCheckinData().getId(),lat, lng, dateCheckIn, 1);//
                                    setCheckInPresences(lastRowId[0]);
                                    checkin_button.setText(getActivity().getString(R.string.checkout));
                                    setupUI();
                                }
                            }
                        });
                    }

                    @Override
                    public void onResponseError(String response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                lastRowId[0] = getGlobalManager().getDatabaseManager().checkINUser(finalWorkplace.getWorkplaceId(), 0,lat, lng, dateCheckIn, 0);
                                setCheckInPresences(lastRowId[0]);
                                setupUI();
                            }
                        });
                    }
                });
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        dialog.dismiss();
    }

    private void setCheckInPresences(long lartRowId) {
        HistoryDataModel historyDataModel = getGlobalManager().getDatabaseManager().getDataQuery(HistoryDataModel.class, "SELECT * FROM employee_precences_history order by id DESC LIMIT 0,1;");
        String historyDataModelJson = new Gson().toJson(historyDataModel, HistoryDataModel.class);
        getGlobalManager().getSharedPreferencesManager().setHistoryDataModelPresences(historyDataModel);
        getGlobalManager().getSharedPreferencesManager().setIsCheckIn(true);
    }

    private void initUI() {
        this.checkin_button = (Button) view.findViewById(R.id.checkin_button);
        this.tv_checkin_date = (TextView) view.findViewById(R.id.tv_checkin_date);
        this.tv_checkin_site = (TextView) view.findViewById(R.id.tv_checkin_site);
        this.tv_checkout_date = (TextView) view.findViewById(R.id.tv_checkout_date);
    }

}
