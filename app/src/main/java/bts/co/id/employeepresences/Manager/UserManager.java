package bts.co.id.employeepresences.Manager;

import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bts.co.id.employeepresences.Activity.View.MainActivity;
import bts.co.id.employeepresences.Listener.ApiListener;
import bts.co.id.employeepresences.Model.Auth;
import bts.co.id.employeepresences.Model.User;

/**
 * Created by IT on 7/14/2016.
 */
public class UserManager extends GlobalManager {

    public ApiManager apiManager;

    public UserManager(Context context) {
        super(context);
        apiManager = new ApiManager(context);
    }

    public void userLogin(String nik, String password, ApiListener listener) {
        apiManager.Login(nik, password, listener);
    }

    public boolean checkUserLogin() {
//        Log.e("isLogin",String.valueOf(this.getSharedPreferencesManager().isLogin()));
        Auth userAuth = this.getSharedPreferencesManager().getUserAuth();
        User user = this.getSharedPreferencesManager().getUserLogin();
        boolean isLogin = this.getSharedPreferencesManager().getIsLogin();

        if (!isLogin) {
            return false;
        }

        if (user == null) {
            return false;
        }

        if (userAuth == null) {
            return false;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateExp = null;
        try {
            dateExp = df.parse(userAuth.getExp());
//            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (getSharedPreferencesManager().isLogin() && dateExp.after(getDateCurrentDateTime()) && !userAuth.getExp().toString().isEmpty()) {
            return true;
        }

        return false;
    }

    public void userLogout() {
        this.getSharedPreferencesManager().clear();
        showNotification("You have been log out", context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    private void checkDateExp(Date dateTime) {

    }
}
