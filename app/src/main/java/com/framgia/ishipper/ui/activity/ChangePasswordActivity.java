package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ChangePasswordData;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends ToolbarActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.edt_new_password) EditText mEdtNewPassword;
    @BindView(R.id.edt_confirm_password) EditText mEdtConfirmPassword;
    @BindView(R.id.btn_change_password) Button mBtnChangePassword;
    @BindView(R.id.edt_old_password) EditText mEdtOldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_change_password;
    }


    @OnClick(R.id.btn_change_password)
    public void onClick() {
        User user = LoginActivity.sUser;
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.ChangePassword.PARAM_PHONE_NUMBER, user.getPhoneNumber());
        params.put(APIDefinition.ChangePassword.PARAM_CURRENT_PASSWORD, mEdtOldPassword.getText().toString());
        params.put(APIDefinition.ChangePassword.PARAM_PASSWORD, mEdtNewPassword.getText().toString());
        params.put(APIDefinition.ChangePassword.PARAM_PASSWORD_CONFIRMATION, mEdtConfirmPassword.getText().toString());
        API.changePassword(
                user.getAuthenticationToken(),
                params,
                new API.APICallback<APIResponse<ChangePasswordData>>() {

                    @Override
                    public void onResponse(APIResponse<ChangePasswordData> response) {
                        Toast.makeText(getBaseContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
