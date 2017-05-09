package bts.co.id.employeepresences.Activity.View;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.rey.material.widget.Button;

import bts.co.id.employeepresences.Activity.BasickActivity;
import bts.co.id.employeepresences.Activity.Controller.LoginActivityController;
import bts.co.id.employeepresences.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BasickActivity implements View.OnClickListener {

    private LoginActivityController controller;

    private EditText edtNik, edtPassword;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        this.controller = new LoginActivityController(this);
    }

    private void setupUi() {
//        removeActionBar();
        this.setContentView(R.layout.activity_login);
        this.edtNik = (EditText) findViewById(R.id.edtNik);
        this.edtPassword = (EditText) findViewById(R.id.edtPassword);

        this.edtNik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtNik.getText().toString().isEmpty()) {
                    edtNik.setError(getApplicationContext().getString(R.string.prompt_error_insert_nik));
                } else {
                    edtNik.setError(null);
                }
            }
        });

        this.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtPassword.getText().toString().isEmpty()) {
                    edtPassword.setError(getApplicationContext().getString(R.string.prompt_error_insert_password));
                } else {
                    edtPassword.setError(null);
                }
            }
        });

        this.btnSignIn = (Button) findViewById(R.id.btnSignIn);
        this.btnSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                boolean oke = true;
                if (edtNik.getText().toString().isEmpty()) {
                    edtNik.setError(getApplicationContext().getString(R.string.prompt_error_insert_nik));
                    oke = false;
                } else {
                    edtNik.setError(null);
                }
                if (edtPassword.getText().toString().isEmpty()) {
                    edtPassword.setError(getApplicationContext().getString(R.string.prompt_error_insert_password));
                    oke = false;
                } else {
                    edtPassword.setError(null);
                }

                if (oke) {
                    controller.checkLogin(edtNik, edtPassword);
                }
                break;
        }
    }
}

