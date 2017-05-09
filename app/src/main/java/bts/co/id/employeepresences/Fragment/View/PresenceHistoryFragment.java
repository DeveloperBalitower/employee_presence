package bts.co.id.employeepresences.Fragment.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rey.material.widget.Button;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bts.co.id.employeepresences.Fragment.BasickFragment;
import bts.co.id.employeepresences.Fragment.Controller.PresenceHistoryFragmentController;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.R;
import bts.co.id.employeepresences.UserInterface.ClearableDatePickerDialog;

/**
 * Created by IT on 7/19/2016.
 */
public class PresenceHistoryFragment extends BasickFragment {

    private View view;
    private PresenceHistoryFragmentController controller;

    private Button btn_search;
    private EditText date_from, date_to;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_presence_history, null);
        this.controller = new PresenceHistoryFragmentController(this);
        initUI();
        return view;
    }

    private void initUI() {
        this.btn_search = (Button) view.findViewById(R.id.btn_search);
        this.date_from = (EditText) view.findViewById(R.id.date_from);
        this.date_to = (EditText) view.findViewById(R.id.date_to);

        this.date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearableDatePickerDialog clpd = new ClearableDatePickerDialog();
                clpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("d-M-yyyy");
                        Date newDate = null;
                        String strDate = "";
                        try {
                            newDate = inputFormat.parse(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            strDate = sdf.format(newDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.e("Date T0 = " + strDate);
                        date_from.setText(strDate);
                    }
                });

                clpd.setOnDateClearedListener(new ClearableDatePickerDialog.OnDateClearedListener() {
                    @Override
                    public void onDateCleared(ClearableDatePickerDialog view) {
                        date_from.setText("");
                    }
                });

                clpd.show(getActivity().getFragmentManager(), "Date From");
            }
        });

        this.date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (date_from.getText().toString().isEmpty()) {
                    date_from.setError("Please select From Date First");
                    date_to.setText("");
                    return;
                }

                Date dateFrom = new Date();
                try {
                    dateFrom = sdf.parse(date_from.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calFrom = Calendar.getInstance();
                calFrom.setTime(dateFrom);

                ClearableDatePickerDialog clpd = new ClearableDatePickerDialog();
                clpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("d-M-yyyy");
                        Date newDate = null;
                        String strDate = "";
                        try {
                            newDate = inputFormat.parse(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            strDate = sdf.format(newDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.e("Date T0 = " + strDate);
                        date_to.setText(strDate);
                    }
                });

                clpd.setOnDateClearedListener(new ClearableDatePickerDialog.OnDateClearedListener() {
                    @Override
                    public void onDateCleared(ClearableDatePickerDialog view) {
                        date_to.setText("");
                    }
                });

                clpd.setMinDate(calFrom);
                clpd.show(getActivity().getFragmentManager(), "Date To");
            }
        });

        this.btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!date_to.getText().toString().isEmpty()) {
                    if (date_from.getText().toString().isEmpty()) {
                        Snackbar.make(view, "Please select Date From", Snackbar.LENGTH_LONG)
                                .setAction("Delete", null).show();
                        return;
                    }
                }
                controller.searchData(date_from.getText().toString(), date_to.getText().toString());

            }
        });
    }
}
