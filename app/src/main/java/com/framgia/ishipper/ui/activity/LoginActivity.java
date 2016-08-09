package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    public static User sUser = new User();
    private static final String TAG = "LoginActivity";
    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.edtPassword) EditText mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getConfig();
    }

    @OnClick({R.id.btnShop, R.id.btnShipper, R.id.btnRegister, R.id.btnForgotPass, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnShop:
                MainActivity.userType = MainActivity.SHOP;
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.btnShipper:
                MainActivity.userType = MainActivity.SHIPPER;
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
            case R.id.btnForgotPass:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.btnLogin:
                HashMap<String, String> params = new HashMap<>();
                params.put(APIDefinition.SignIn.PARAM_PHONE_NUMBER, mEdtPhoneNumber.getText().toString());
                params.put(APIDefinition.SignIn.PARAM_PASSWORD, mEdtPassword.getText().toString());
                API.signIn(params, new API.APICallback<APIResponse<APIResponse.SignInResponse>>() {
                    @Override
                    public void onResponse(APIResponse<APIResponse.SignInResponse> response) {
                        Toast.makeText(getBaseContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        sUser = response.getData().getUser();
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }

    private void getConfig() {
        Config.SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
        Config.SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
    }
}
