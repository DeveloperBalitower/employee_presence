package bts.co.id.employeepresences.Manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Model.RETURN_DATA;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by IT on 7/14/2016.
 */
public class ApiManager extends GlobalManager {

        public static String BASE_URL = "https://devemployeepresence.balitower.co.id/"; /* Publish Domain Development */
//    public static String BASE_URL = "http://172.16.2.174/bts_user_presence/"; /* Local Pc Domain Development */
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(new LoggingInterceptor())
            .build();
    private String JWT_KEY = "BTS ";


    public ApiManager(Context context) {
        super(context);
    }

    public void errorHandling(Response response, ApiListener listener) {
        final Gson gson = new Gson();
        if (response.code() == 403 || response.code() == 401) {
            this.getUserManager().userLogout();
        }
        if (response.code() == 500) {
            RETURN_DATA return_data = new RETURN_DATA();
            return_data.setMessage("Unknown Error Or Internal Server Error, Please check your connection or contact your Administrator");
            listener.onResponseError(return_data.getMessage());
        } else {
            try {
                String responseString = response.body().string();
                RETURN_DATA return_data = gson.fromJson(responseString, RETURN_DATA.class);
                listener.onResponseError(return_data.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getUserAuth() {
        if (this.getSharedPreferencesManager().getUserAuth().getToken() != null && !this.getSharedPreferencesManager().getUserAuth().getToken().isEmpty()) {
            return JWT_KEY + this.getSharedPreferencesManager().getUserAuth().getToken();
        }
        return "";
    }

    public void getPresencesSetting(final ApiListener listener) {
        String url_base = "user/get_employee_presences_setting";

        String authKey = getUserAuth().toString();

        final Request request = new Request.Builder()
                .url(BASE_URL + url_base)
                .get()
                .addHeader("Authorization", authKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//                    listener.onResponseSuccess(hardcodeReturn);
                    listener.onResponseSuccess(response.body().string());
                } else {
                    errorHandling(response, listener);
                }
            }
        });
    }

    public void Login(String nik, String password, final ApiListener listener) {
        String url_base = "user/login";

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double loc_lat = 0.0;
        double loc_lng = 0.0;

        if (location != null) {
            if (!String.valueOf(location.getLatitude()).isEmpty() && String.valueOf(location.getLatitude()).length() > 0) {
                loc_lat = location.getLatitude();
            }
            if (!String.valueOf(location.getLongitude()).isEmpty() && String.valueOf(location.getLongitude()).length() > 0) {
                loc_lng = location.getLongitude();
            }
        }

        String curDate = getCurrentDateTime();
        String devices_id = getDevicesID();
        String authKey = JWT_KEY + md5(nik + devices_id + curDate);
        RequestBody formBody = new FormBody.Builder()
                .add("nik", nik)
                .add("password", password)
                .add("devices_id", devices_id)
                .add("dt", curDate)
                .add("lat", String.valueOf(loc_lat))
                .add("lng", String.valueOf(loc_lng))
                .build();

        final Request request = new Request.Builder()
                .url(BASE_URL + url_base)
                .post(formBody)
                .addHeader("Authorization", authKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//                    listener.onResponseSuccess(hardcodeReturn);
                    listener.onResponseSuccess(response.body().string());
                } else {
                    errorHandling(response, listener);
                }
            }
        });
    }

    public void user_checkin(String site_id, String date, double lat, double lng, final ApiListener listener) {
        String url_base = "user/check_in";

        RequestBody formBody = new FormBody.Builder()
                .add("dt", date)
                .add("site", site_id)
                .add("lat", String.valueOf(lat))
                .add("lng", String.valueOf(lng))
                .build();

        final Request request = new Request.Builder()
                .url(BASE_URL + url_base)
                .post(formBody)
                .addHeader("Authorization", getUserAuth().toString())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//                    listener.onResponseSuccess(hardcodeReturn);
                    listener.onResponseSuccess(response.body().string());
                } else {
                    errorHandling(response, listener);
                }
            }
        });
    }

    public void uploadLocalDataToServer(String jsonData, final ApiListener listener) {
        String url_base = "upload/local_presence";
        String devices_id = getDevicesID();
        final String date = getCurrentDateTime();
        RequestBody formBody = new FormBody.Builder()
                .add("history", jsonData)
                .add("devices_id", devices_id)
                .add("dt", date)
                .build();

        final Request request = new Request.Builder()
                .url(BASE_URL + url_base)
                .post(formBody)
                .addHeader("Authorization", getUserAuth().toString())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//                    listener.onResponseSuccess(hardcodeReturn);
                    listener.onResponseSuccess(response.body().string());
                } else {
                    errorHandling(response, listener);
                }
            }
        });
    }

    public void user_checkOut(String site_id, int server_id, String dateCheckin, String dateCheckOut, final ApiListener listener) {
        String url_base = "user/check_out";

        RequestBody formBody = new FormBody.Builder()
                .add("dtcheckin", dateCheckin)
                .add("dtcheckout", dateCheckOut)
                .add("his_id", String.valueOf(server_id))
                .add("site", site_id)
                .build();

        final Request request = new Request.Builder()
                .url(BASE_URL + url_base)
                .post(formBody)
                .addHeader("Authorization", getUserAuth().toString())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//                    listener.onResponseSuccess(hardcodeReturn);
                    listener.onResponseSuccess(response.body().string());
                } else {
                    Log.d("response.code", String.valueOf(response.code()));
                    errorHandling(response, listener);
                }
            }
        });
    }

    public void getUserHistory(String date_f, String date_t, final ApiListener listener) {
        String url_base = "user/get_presencess_history";

        RequestBody formBody = new FormBody.Builder()
                .add("date_f", date_f)
                .add("date_t", date_t)
                .build();

        final Request request = new Request.Builder()
                .url(BASE_URL + url_base)
                .post(formBody)
                .addHeader("Authorization", getUserAuth().toString())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
//                    listener.onResponseSuccess(hardcodeReturn);
                    listener.onResponseSuccess(response.body().string());
                } else {
                    errorHandling(response, listener);
                }
            }
        });
    }
}