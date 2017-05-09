package bts.co.id.employeepresences.Activity.Controller;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import bts.co.id.employeepresences.Activity.View.HomeActivity;
import bts.co.id.employeepresences.Activity.View.LoginActivity;
import bts.co.id.employeepresences.Activity.View.MainActivity;
import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Manager.DatabaseManager;
import bts.co.id.employeepresences.Manager.GlobalManager;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.ListHistoryDataModel;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import bts.co.id.employeepresences.Model.SystemSetting;
import bts.co.id.employeepresences.Model.WorkPlaces;
import bts.co.id.employeepresences.R;
import okhttp3.Request;

/**
 * Created by IT on 7/14/2016.
 */
public class MainActivityController {
    private MainActivity activity;
    private GlobalManager globalManager;

//    private SpotsDialog dialog;

    public MainActivityController(MainActivity activity) {
        this.activity = activity;
        this.globalManager = new GlobalManager(this.activity);
//        dialog = this.activity.getGlobalManager().getUiManager().getSpotsDialog(this.activity);

    }

    public void startAnimImage(ImageView imageView) {
        //ImageView image = (ImageView)findViewById(R.id.imageView);
        Animation animation1 = AnimationUtils.loadAnimation(activity, R.anim.fade);
        imageView.startAnimation(animation1);
        startTimerSplas();
    }

    public void startTimerSplas() {
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (DatabaseManager.getInstance().isDatabaseExists() && globalManager.getUserManager().checkUserLogin()) {
                                processing();
                            } else {
                                globalManager.getSharedPreferencesManager().clear();
                                processing();
                            }
                        }
                    });
                }
            }
        };
        timerThread.start();
    }

    private void processing() {
        if (globalManager.getUserManager().checkUserLogin()) {
//            final AlertDialog dialog = activity.getGlobalManager().getUiManager().getSpotsDialog(activity);
//            dialog.setTitle("Please Wait");
//            dialog.show();
            ListHistoryDataModel historyDataModels = new ListHistoryDataModel();
            historyDataModels.setHistoryDataModels(globalManager.getDatabaseManager().getPresencesListToUpload());
            if (historyDataModels != null && historyDataModels.getHistoryDataModels().size() > 0) {
                String jsonString = new Gson().toJson(historyDataModels, ListHistoryDataModel.class);
                Log.i("jsonString = " + jsonString);
                globalManager.getApiManager().uploadLocalDataToServer(jsonString, new ApiListener() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("Local Upload Failure");
//                        globalManager.getDatabaseManager().deleteAllDataHistory();
                    }

                    @Override
                    public void onResponseSuccess(String responseBody) throws IOException {
                        Log.e("Local Upload Success " + responseBody);
                        globalManager.getDatabaseManager().deleteAllDataHistory();
                        RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);
                        globalManager.getDatabaseManager().deleteAllDataWorkPlaces();
                        globalManager.getDatabaseManager().insertWorkplaces(return_data.getData().getWorkplaces());

                        globalManager.getDatabaseManager().deleteAllDataHistory();
                        globalManager.getDatabaseManager().deleteAllDataSetting();
                        globalManager.getDatabaseManager().insertPresencesHistory(return_data.getData().getPresencesHistory(), 1);
                        globalManager.getDatabaseManager().insertEmployeeSetting(return_data.getData().getSystemSetting());
                    }

                    @Override
                    public void onResponseError(String response) {
                        Log.e("Local Upload Error " + response);
//                        globalManager.getDatabaseManager().deleteAllDataHistory();
                    }
                });
            }

            globalManager.getApiManager().getPresencesSetting(new ApiListener() {
                @Override
                public void onFailure(Request request, IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toActivity(HomeActivity.class);
//                            dialog.dismiss();
                        }
                    });
                }

                @Override
                public void onResponseSuccess(String responseBody) throws IOException {

                    RETURN_DATA return_data = new Gson().fromJson(responseBody, RETURN_DATA.class);

                    if (return_data != null && return_data.getData() != null) {
                        List<SystemSetting> presencesSetting = return_data.getData().getSystemSetting();
                        int versionDbServer = 1;

                        globalManager.getDatabaseManager().insertPresencesHistory(return_data.getData().getPresencesHistory(), 1);
                        if (presencesSetting.size() > 0) {
                            for (int i = 0; i < presencesSetting.size(); i++) {
                                if (presencesSetting.get(i).getSettingName().equalsIgnoreCase("mysql_db_version")) {
                                    versionDbServer = Integer.valueOf(presencesSetting.get(i).getValueInt());
                                    break;
                                }
                            }
                        }

                        if (versionDbServer > globalManager.getDatabaseManager().getDbServerVersion()) {
                            globalManager.getDatabaseManager().deleteAllDataWorkPlaces();
                            WorkPlaces workPlaces = new WorkPlaces();
                            workPlaces.setWorkplaces(return_data.getData().getWorkplaces());//
                            globalManager.getDatabaseManager().insertEmployeeSetting(return_data.getData().getSystemSetting());
                            globalManager.getDatabaseManager().insertWorkplaces(workPlaces.getWorkplaces());
                        }

                        globalManager.getSharedPreferencesManager().setUserLoginPref(return_data.getData().getUser().get(0));
                        globalManager.getSharedPreferencesManager().setUserAuth(return_data.getData().getAuth());
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toActivity(HomeActivity.class);
//                            dialog.dismiss();
                        }
                    });

                }

                @Override
                public void onResponseError(String response) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toActivity(HomeActivity.class);
                        }
                    });
                }
            });
        } else {
            toActivity(LoginActivity.class);
        }
    }

    private void toActivity(Class<?> actCls) {
        Intent intent = new Intent(activity, actCls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
