package bts.co.id.employeepresences.Activity.Controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;

import bts.co.id.employeepresences.Activity.View.HomeActivity;
import bts.co.id.employeepresences.Activity.View.LoginActivity;
import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import bts.co.id.employeepresences.Model.WorkPlaces;
import okhttp3.Request;

/**
 * Created by IT on 7/14/2016.
 */
public class LoginActivityController {

    private LoginActivity activity;
    private GlobalManager globalManager;

    public LoginActivityController(LoginActivity activity) {
        this.activity = activity;
        this.globalManager = this.activity.getGlobalManager();
    }

    public void checkLogin(EditText editTextNIK, EditText editTextPassword) {
        final AlertDialog dialog = this.activity.getGlobalManager().getUiManager().getSpotsDialog(this.activity);
        dialog.show();
//        Log.i("ConnectionCheck", String.valueOf(this.globalManager.getConnectionManager().checkConnectivity()));
//        if (!this.globalManager.getConnectionManager().checkConnectivity()) {
//            dialog.dismiss();
//            globalManager.getUiManager().ShowAlertDialogNotification("Connection Error", "Failed to connect to server try again.", "OK");
//            return;
//        }
        this.globalManager.getUserManager().userLogin(editTextNIK.getText().toString(), editTextPassword.getText().toString(), new ApiListener() {
            @Override
            public void onFailure(Request request, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        globalManager.getUiManager().ShowAlertDialogNotification("Connection Error", "Connection error please try again.", "OK");
                    }
                });
            }

            @Override
            public void onResponseSuccess(final String responseBody) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (!responseBody.isEmpty() && responseBody != null) {
                            int versionDbServer = 1;

                            RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);

                            WorkPlaces workPlaces = new WorkPlaces();
                            workPlaces.setWorkplaces(return_data.getData().getWorkplaces());
                            globalManager.getDatabaseManager().deleteAllDataWorkPlaces();
                            globalManager.getDatabaseManager().insertWorkplaces(workPlaces.getWorkplaces());

                            globalManager.getDatabaseManager().deleteAllDataHistory();
                            globalManager.getDatabaseManager().insertPresencesHistory(return_data.getData().getPresencesHistory(), 1);
                            globalManager.getDatabaseManager().insertEmployeeSetting(return_data.getData().getSystemSetting());

                            globalManager.getSharedPreferencesManager().setIsLogin(true);
                                globalManager.getSharedPreferencesManager().setUserLoginPref(return_data.getData().getUser().get(0));
                            globalManager.getSharedPreferencesManager().setUserAuth(return_data.getData().getAuth());

                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.putExtra("fromLogin", true);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            globalManager.getUiManager().ShowAlertDialogNotification("Login Error", "Please Try Again", "OK");
                        }
                    }
                });
            }

            @Override
            public void onResponseError(final String response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        globalManager.getUiManager().ShowAlertDialogNotification("Login Error", response, "OK");
                    }
                });
            }
        });
    }
}
