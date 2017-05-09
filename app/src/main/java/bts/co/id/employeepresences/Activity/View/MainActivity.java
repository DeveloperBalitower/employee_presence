package bts.co.id.employeepresences.Activity.View;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import bts.co.id.employeepresences.Activity.BasickActivity;
import bts.co.id.employeepresences.Activity.Controller.MainActivityController;
import bts.co.id.employeepresences.R;

public class MainActivity extends BasickActivity {

    private ImageView imageViewLogo;
    private MainActivityController controller;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        uiSetup();
        this.controller = new MainActivityController(this);
        this.controller.startAnimImage(this.imageViewLogo);
    }

    private void uiSetup() {
//        removeActionBar();
        this.setContentView(R.layout.activity_main);
        this.imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        this.tv_version = (TextView) findViewById(R.id.tv_version);
        this.tv_version.setText("Version " + getGlobalManager().getVersionNumber());
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
