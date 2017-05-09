package bts.co.id.employeepresences.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import bts.co.id.employeepresences.Activity.View.HomeActivity;
import bts.co.id.employeepresences.Manager.GlobalManager;

/**
 * Created by IT on 7/19/2016.
 */
public class BasickFragment extends Fragment {
    private GlobalManager globalManager;
    private HomeActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (HomeActivity) getActivity();
        this.globalManager = new GlobalManager(this.getContext());
    }

    public GlobalManager getGlobalManager() {
        return globalManager;
    }

    public HomeActivity getMainActivity() {
        return activity;
    }
}
