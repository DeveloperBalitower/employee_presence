package bts.co.id.employeepresences.Activity.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.rey.material.widget.Button;

import bts.co.id.employeepresences.R;

/**
 * Created by Andreas Panjaitan on 8/5/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class HomeScreenDialogActivity extends Activity {
    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    Button mOKBtn;
    TextView tv_messages;
    View.OnClickListener mDialogbuttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ID_Ok) {
                mAlertDialog.dismiss();
                finish();
            }
//            else if(v.getId() == R.id.ID_Cancel)
//            {
//                mAlertDialog.dismiss();
//                finish();
//            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = getLayoutInflater();

        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.home_screen_dialog_layout, null);
        mOKBtn = (Button) mDialogView.findViewById(R.id.ID_Ok);
//        mCancelBtn = (Button)mDialogView.findViewById(R.id.ID_Cancel);
        tv_messages = (TextView) mDialogView.findViewById(R.id.tv_messages);
        mOKBtn.setOnClickListener(mDialogbuttonClickListener);
//        mCancelBtn.setOnClickListener(mDialogbuttonClickListener);
        mAlertDlgBuilder.setCancelable(false);
        mAlertDlgBuilder.setInverseBackgroundForced(true);
        mAlertDlgBuilder.setView(mDialogView);
        mAlertDialog = mAlertDlgBuilder.create();
        mAlertDialog.show();

    }
}