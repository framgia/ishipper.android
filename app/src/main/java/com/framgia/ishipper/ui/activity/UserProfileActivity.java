package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
    @BindView(R.id.edt_profile_phone) TextInputEditText mEdtProfilePhone;
    @BindView(R.id.edt_profile_address) TextInputEditText mEdtProfileAddress;
    private User mCurrentUser;
    private TextView mEdtProfilePassword;
    private TextView mDialogPasswordOk;
    private TextView mDialogPasswordCancel;
    private AlertDialog mInputPasswordDialog;

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
        mCurrentUser = Config.getInstance().getUserInfo(getApplicationContext());
        mEdtProfilePlate.setText(mCurrentUser.getPlateNumber());
        mEdtProfileName.setText(mCurrentUser.getName());
        mEdtProfilePhone.setText(mCurrentUser.getPhoneNumber());
        mEdtProfileAddress.setText(mCurrentUser.getAddress());
        if (mCurrentUser.getRole().equals(User.ROLE_SHOP)) {
            mEdtProfilePlate.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.tv_change_password, R.id.btn_profile_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.btn_profile_update:
                showPasswordDialog();
                break;
        }
    }

    private void showPasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_password, null);
        mEdtProfilePassword = (TextView) view.findViewById(R.id.edt_profile_password);
        mDialogPasswordOk = (TextView) view.findViewById(R.id.dialog_password_ok);
        mDialogPasswordCancel = (TextView) view.findViewById(R.id.dialog_password_cancel);
        mDialogPasswordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
                mInputPasswordDialog.cancel();
            }
        });
        mDialogPasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputPasswordDialog.cancel();
            }
        });
        mInputPasswordDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .show();
    }

    private void updatePassword() {
        HashMap<String, String> params = new HashMap<>();
        params.put(APIDefinition.PutUpdateProfile.PARAM_NAME, mEdtProfileName.getText().toString());
        params.put(APIDefinition.PutUpdateProfile.USER_CURRENT_PASSWORD, mEdtProfilePassword.getText().toString());
        params.put(APIDefinition.PutUpdateProfile.PARAM_PHONE_NUMBER, mEdtProfilePhone.getText().toString());
        params.put(APIDefinition.PutUpdateProfile.PARAM_PLATE_NUMBER, mEdtProfilePlate.getText().toString());
        params.put(APIDefinition.PutUpdateProfile.PARAM_ADDRESS, mEdtProfileAddress.getText().toString());

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
    }

}
