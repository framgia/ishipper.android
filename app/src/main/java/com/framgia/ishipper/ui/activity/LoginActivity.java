package com.framgia.ishipper.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.SignInData;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.Const;
import com.framgia.ishipper.util.Const.Firebase;
import com.framgia.ishipper.util.InputValidate;
import com.framgia.ishipper.util.StorageUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.edtPassword) EditText mEdtPassword;
    @BindView(R.id.spnPrefixPhoneNumber) Spinner mSpnPrefixPhoneNumber;

    private String mPrefixPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getConfig();
        // Check login
        if (Config.getInstance().isLogin(getApplicationContext())) {
            startMainActivity();
        }

        // Register news topic that user can be received all news, promotion, ...
        FirebaseMessaging.getInstance().subscribeToTopic(Firebase.TOPIC_NEWS);

        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setUpSpinner();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.prefix_phone_number,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnPrefixPhoneNumber.setAdapter(adapter);
        mSpnPrefixPhoneNumber.setOnItemSelectedListener(this);
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
                break;
            case R.id.btnForgotPass:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.btnLogin:
                login();
                break;
        }
    }

    private void login() {

        String phoneNumber = mPrefixPhoneNumber + mEdtPhoneNumber.getText().toString();
        if (!InputValidate.checkPhoneNumber(mEdtPhoneNumber, this)) {
            return;
        }
        if (!InputValidate.checkPassword(mEdtPassword, this)) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put(APIDefinition.SignIn.PARAM_PHONE_NUMBER, phoneNumber);
        params.put(APIDefinition.SignIn.PARAM_PASSWORD, mEdtPassword.getText().toString());
        final Dialog loadingDialog = CommonUtils.showLoadingDialog(this);
        API.signIn(params, new API.APICallback<APIResponse<SignInData>>() {
            @Override
            public void onResponse(APIResponse<SignInData> response) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), response.getMessage(),
                        Toast.LENGTH_SHORT).show();
                User user = response.getData().getUser();
                Config.getInstance().setUserInfo(getApplicationContext(), user);

                API.putFCMRegistrationID(user.getAuthenticationToken(),
                        StorageUtils.getStringValue(
                                getBaseContext(),
                                Const.Storage.KEY_NOTIFICATION_ID),
                        new API.APICallback<APIResponse<EmptyData>>() {
                            @Override
                            public void onResponse(APIResponse<EmptyData> response) {

                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });

                startMainActivity();
            }

            @Override
            public void onFailure(int code, String message) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Get screen size config
     */
    private void getConfig() {
        Const.SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
        Const.SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mPrefixPhoneNumber = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        mPrefixPhoneNumber = "";
    }

}
