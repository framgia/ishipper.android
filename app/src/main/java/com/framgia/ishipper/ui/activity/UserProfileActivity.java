package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.UpdateProfileData;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends ToolbarActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.edt_profile_name) TextInputEditText mEdtProfileName;
    @BindView(R.id.edt_profile_plate) TextInputEditText mEdtProfilePlate;
    @BindView(R.id.edt_profile_password) TextInputEditText mEdtProfilePassword;
    @BindView(R.id.edt_profile_phone) TextInputEditText mEdtProfilePhone;
    @BindView(R.id.edt_profile_address) TextInputEditText mEdtProfileAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        bindData();
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.nav_user_name_example;
    }

    private void bindData() {
        User user = Config.getInstance().getUserInfo(getApplicationContext());
        mEdtProfilePlate.setText(user.getPlateNumber());
        mEdtProfileName.setText(user.getName());
        mEdtProfilePhone.setText(user.getPhoneNumber());
    }


    @OnClick({R.id.tv_change_password, R.id.btn_profile_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.btn_profile_update:
                HashMap<String, String> params = new HashMap<>();
                params.put(APIDefinition.PutUpdateProfile.PARAM_NAME, mEdtProfileName.getText().toString());
                params.put(APIDefinition.PutUpdateProfile.USER_CURRENT_PASSWORD, mEdtProfilePassword.getText().toString());
                params.put(APIDefinition.PutUpdateProfile.PARAM_PHONE_NUMBER, mEdtProfilePhone.getText().toString());
                params.put(APIDefinition.PutUpdateProfile.PARAM_PLATE_NUMBER, mEdtProfilePlate.getText().toString());

                API.putUpdateProfile(params, new API.APICallback<APIResponse<UpdateProfileData>>() {
                    @Override
                    public void onResponse(APIResponse<UpdateProfileData> response) {
                        Config.getInstance().setUserInfo(getApplicationContext(), response.getData().user);
                        Toast.makeText(UserProfileActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
