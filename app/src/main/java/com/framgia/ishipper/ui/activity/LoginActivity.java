package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.framgia.ishipper.Config;
import com.framgia.ishipper.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getConfig();
    }

    @OnClick({R.id.btnShop, R.id.btnShipper, R.id.btnRegister, R.id.btnForgotPass})
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
                break;
            case R.id.btnForgotPass:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
        }

    }

    private void getConfig() {
        Config.SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
        Config.SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
    }
}
