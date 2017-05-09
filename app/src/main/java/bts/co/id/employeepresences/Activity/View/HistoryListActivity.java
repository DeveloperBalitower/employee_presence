package bts.co.id.employeepresences.Activity.View;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bts.co.id.employeepresences.Activity.BasickActivity;
import bts.co.id.employeepresences.Activity.Controller.HistoryListActivityController;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import bts.co.id.employeepresences.Model.User;
import bts.co.id.employeepresences.R;
import bts.co.id.employeepresences.UserInterface.AutoResizeTextView;
//import bts.co.id.employeepresences.UserInterface.AutoResizeTextView;

public class HistoryListActivity extends BasickActivity {

    List<HistoryDataModel> presencesHistories;
    private Toolbar toolbar;
    private TextView tv_nik, tv_name, tv_periode;
    private TableLayout list_tabel;
    private HistoryListActivityController controller;
    private String dataHistory;
    private String periode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        controller = new HistoryListActivityController(this);
        getBundle();
        setUpUI();
        setupTableHistory();
    }

    private void setupTableHistory() {

        TableRow rowHeader = new TableRow(this);
        rowHeader.setBackgroundResource(R.color.blue_180);

        TextView headerLocation = new TextView(this);
        headerLocation.setBackgroundResource(R.color.transparent);
        headerLocation.setText("Location");
        headerLocation.setSingleLine(true);
        headerLocation.setMaxLines(1);
        headerLocation.setPadding(this.getGlobalManager().getDpToInt(0), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(0));
        headerLocation.setGravity(Gravity.CENTER);
        headerLocation.setTextColor(getResources().getColor(R.color.white));
        rowHeader.addView(headerLocation);

        TextView headerCheckIn = new TextView(this);
        headerCheckIn.setBackgroundResource(R.color.transparent);
        headerCheckIn.setText("Check In");
        headerCheckIn.setPadding(this.getGlobalManager().getDpToInt(0), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(0));
        headerCheckIn.setGravity(Gravity.CENTER);
        headerCheckIn.setMaxLines(1);
        headerCheckIn.setSingleLine(true);
        headerCheckIn.setTextColor(getResources().getColor(R.color.white));
        rowHeader.addView(headerCheckIn);

        TextView headerCheckOut = new TextView(this);
        headerCheckOut.setBackgroundResource(R.color.transparent);
        headerCheckOut.setText("Check Out");
        headerCheckOut.setPadding(this.getGlobalManager().getDpToInt(0), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(0));
        headerCheckOut.setGravity(Gravity.CENTER);
        headerCheckOut.setSingleLine(true);
        headerCheckOut.setMaxLines(1);
        headerCheckOut.setTextColor(getResources().getColor(R.color.white));
        rowHeader.addView(headerCheckOut);

        list_tabel.addView(rowHeader);

        if (presencesHistories.size() < 1) {
            TableRow rowAsset = new TableRow(this);
            rowAsset.setBackgroundResource(R.drawable.background_row_ganjil);

            // TextView For Location
            final AutoResizeTextView location = new AutoResizeTextView(this);
            location.setText("Data Not Found");
            location.setPadding(this.getGlobalManager().getDpToInt(10), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5));
            location.setGravity(Gravity.CENTER);
            rowAsset.addView(location);
            list_tabel.addView(rowAsset);

            TableRow.LayoutParams params = (TableRow.LayoutParams) location.getLayoutParams();
            params.span = 3;
            location.setLayoutParams(params);
            return;
        }

        for (int i = 0; i < presencesHistories.size(); i++) {
            HistoryDataModel presencesHistory = presencesHistories.get(i);
            TableRow rowAsset = new TableRow(this);
            rowAsset.setId(i);
            int fontColor = getResources().getColor(R.color.black);
            if (i % 2 == 0) {
                rowAsset.setBackgroundResource(R.color.white);
                fontColor = getResources().getColor(R.color.black_gray);
            } else {
                rowAsset.setBackgroundResource(R.color.gray_border);
                fontColor = getResources().getColor(R.color.white);
            }

//            rowAsset.setBackgroundResource(R.drawable.gray_border)

            // TextView For Location

            String[] locationStringArray = presencesHistory.getLocationName().split("[-]");
            String locationText = "";
            for (int x = 0; x < locationStringArray.length; x++) {
                locationText += locationStringArray[x];
                if (x > 0) {
                    locationText += "\n";
                }
                locationText += " ";
            }

            final AutoResizeTextView location = new AutoResizeTextView(this);
            location.setBackgroundResource(R.color.transparent);
            location.setText(locationText);
            location.setId(i);
            location.setPadding(this.getGlobalManager().getDpToInt(0), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5));
            location.setGravity(Gravity.CENTER);
            location.setSingleLine(false);
            location.setTextColor(fontColor);
            rowAsset.addView(location);

            // TextView For Check In
            final AutoResizeTextView checkIn = new AutoResizeTextView(this);
            checkIn.setBackgroundResource(R.color.transparent);

            String dateCheckin = "";
            String dateCheckOut = "";

            if (presencesHistory.getCheck_in_date() != null && !presencesHistory.getCheck_in_date().isEmpty()) {
                String strCurrentDate = presencesHistory.getCheck_in_date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = null;
                try {
                    newDate = format.parse(strCurrentDate);
                    format = new SimpleDateFormat("dd-MM-yy");
                    dateCheckin = format.format(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (presencesHistory.getCheck_in_time() == null) {
                presencesHistory.setCheck_in_time("");
            }


            checkIn.setText(dateCheckin + "\n" + presencesHistory.getCheck_in_time());
            checkIn.setId(i);
            checkIn.setPadding(this.getGlobalManager().getDpToInt(0), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5));
            checkIn.setGravity(Gravity.CENTER);
            checkIn.setMaxLines(2);
            checkIn.setTextColor(fontColor);
            rowAsset.addView(checkIn);

            // TextView For Check Out
            final AutoResizeTextView checkOut = new AutoResizeTextView(this);
            checkOut.setBackgroundResource(R.color.transparent);

            if (presencesHistory.getCheck_out_date() != null && !presencesHistory.getCheck_out_date().isEmpty()) {
                String strCurrentDate = presencesHistory.getCheck_out_date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date newDateCheckOut = null;
                try {
                    newDateCheckOut = format.parse(strCurrentDate);
                    format = new SimpleDateFormat("dd-MM-yy");
                    dateCheckOut = format.format(newDateCheckOut);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (presencesHistory.getCheck_out_time() == null) {
                presencesHistory.setCheck_out_time("");
            }

            checkOut.setText(dateCheckOut + "\n" + presencesHistory.getCheck_out_time());
            checkOut.setId(i);
            checkOut.setPadding(this.getGlobalManager().getDpToInt(0), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5), this.getGlobalManager().getDpToInt(5));
            checkOut.setGravity(Gravity.CENTER);
            checkOut.setMaxLines(2);
            checkOut.setTextColor(fontColor);
            rowAsset.addView(checkOut);
            list_tabel.addView(rowAsset);
        }
    }

    private void getBundle() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("dataHistory")) {
                Log.e("setDataHistory", extras.getString("dataHistory"));
                this.dataHistory = extras.getString("dataHistory");
                Log.d("data_history", extras.getString("dataHistory"));
                RETURN_DATA return_data = new Gson().fromJson(this.dataHistory, RETURN_DATA.class);
//                ListPresencesHistory listPresences = return_data.getData().getPresencesHistory();
                presencesHistories = return_data.getData().getPresencesHistory();
            }

            if (extras.containsKey("periode")) {
                this.periode = extras.getString("periode");
            }
        }
    }

    private void setUpUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_nik = (TextView) findViewById(R.id.tv_nik);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_periode = (TextView) findViewById(R.id.tv_periode);
        list_tabel = (TableLayout) findViewById(R.id.list_tabel);

        User dataUser = getGlobalManager().getSharedPreferencesManager().getUserLogin();

        tv_nik.setText(dataUser.getNik());
        tv_name.setText(dataUser.getEmpName());
        tv_periode.setText(this.periode);

    }
}
