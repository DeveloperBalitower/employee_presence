package bts.co.id.employeepresences.Fragment.Controller;

import android.app.AlertDialog;
import android.content.Intent;

import com.google.gson.Gson;

import java.io.IOException;

import bts.co.id.employeepresences.Activity.View.HistoryListActivity;
import bts.co.id.employeepresences.Fragment.View.PresenceHistoryFragment;
import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import okhttp3.Request;

/**
 * Created by IT on 7/19/2016.
 */
public class PresenceHistoryFragmentController {

    private PresenceHistoryFragment fragment;

    public PresenceHistoryFragmentController(PresenceHistoryFragment fragment) {
        this.fragment = fragment;
    }

    public void searchData(final String date_from, final String date_to) {
        final AlertDialog dialog = fragment.getGlobalManager().getUiManager().getSpotsDialog(fragment.getActivity());
        dialog.show();

        fragment.getGlobalManager().getApiManager().getUserHistory(date_from, date_to, new ApiListener() {
            @Override
            public void onFailure(Request request, IOException e) {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        fragment.getGlobalManager().getUiManager().ShowAlertDialogNotification("Connection Error", "Connection error please try again.", "OK");
                    }
                });
            }

            @Override
            public void onResponseSuccess(final String responseBody) throws IOException {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);
                        fragment.getGlobalManager().getSharedPreferencesManager().setUserLoginPref(return_data.getData().getUser().get(0));
                        fragment.getGlobalManager().getSharedPreferencesManager().setUserAuth(return_data.getData().getAuth());
                        Intent intent = new Intent(fragment.getActivity(), HistoryListActivity.class);
                        intent.putExtra("dataHistory", responseBody);
                        String periode = "Periode : ";
                        if (date_from.isEmpty() && date_to.isEmpty()) {
                            periode += "All";
                        } else {
                            periode += date_from + " - " + date_to;
                        }
                        intent.putExtra("periode", periode);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        fragment.getActivity().startActivity(intent);
                    }
                });
            }

            @Override
            public void onResponseError(final String response) {
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Log.e(response);
                    }
                });
            }
        });
    }

}
