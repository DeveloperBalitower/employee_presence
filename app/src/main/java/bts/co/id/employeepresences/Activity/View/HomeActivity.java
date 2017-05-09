package bts.co.id.employeepresences.Activity.View;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import bts.co.id.employeepresences.Activity.BasickActivity;
import bts.co.id.employeepresences.Activity.Controller.HomeActivityController;
import bts.co.id.employeepresences.Fragment.View.HomeFragment;
import bts.co.id.employeepresences.Fragment.View.PresenceHistoryFragment;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.User;
import bts.co.id.employeepresences.R;

public class HomeActivity extends BasickActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HomeActivityController controller;
    private User userModel;
    private int navigationSelect = 0;
    private boolean fromLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.controller = new HomeActivityController(this);
        getBundle();
//        application.startServices();
        userModel = this.getGlobalManager().getSharedPreferencesManager().getUserLogin();
        setupUi();
        startUp();
        setupFirstFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Home", "OnDestroy");
//        application.stopService(new Intent(this.getApplicationContext(),
//                xTrackServices.class));
    }

    private void startUp() {
        if (fromLogin) {

        }
    }

    private void getBundle() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("fromLogin")) {
                this.fromLogin = extras.getBoolean("fromLogin", false);
            }
        }
    }

    private void setupUi() {
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        TextView userNameTextView = (TextView) header.findViewById(R.id.userNameTextView);
        TextView userMailTextView = (TextView) header.findViewById(R.id.userMailTextView);

        userNameTextView.setText(userModel.getEmpName());
        userMailTextView.setText(userModel.getEmail());

    }

    public void setupFirstFragment() {
        Fragment fragment = null;
        if (this.navigationSelect == 0 || this.navigationSelect == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (this.navigationSelect == R.id.nav_history) {
            fragment = new PresenceHistoryFragment();
        }
        changeFragmet(fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        this.navigationSelect = id;
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_history) {
            fragment = new PresenceHistoryFragment();
        } else if (id == R.id.nav_signout) {
            getGlobalManager().getUserManager().userLogout();
            return true;
        }
        changeFragmet(fragment);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragmet(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.containerView, fragment, null);
            fragmentTransaction.commit();
        }
    }
}
