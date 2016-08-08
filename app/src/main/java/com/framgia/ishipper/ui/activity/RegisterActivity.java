package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.ishipper.R;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.server.RegisterResponse;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends ToolbarActivity {

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.edtPasswordRegister) EditText mEdtPasswordRegister;
    @BindView(R.id.edtNameRegister) EditText mEdtNameRegister;
    @BindView(R.id.edtPlateNumber) EditText mEdtPlateNumber;
    @BindView(R.id.radioGroupUserType) RadioGroup mRadioGroupUserType;
    @BindView(R.id.layoutPlate) LinearLayout mLayoutPlate;
    @BindView(R.id.tvTitleName) TextView mTvTitleName;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        configUserType();
    }

    private void configUserType() {
        user.setRole(User.ROLE_SHIPPER);
        mRadioGroupUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioShipper) {
                    mLayoutPlate.setVisibility(View.VISIBLE);
                    mTvTitleName.setText(getString(R.string.name));
                    user.setRole(User.ROLE_SHIPPER);
                } else {
                    mLayoutPlate.setVisibility(View.GONE);
                    mTvTitleName.setText(R.string.name_shop);
                    user.setRole(User.ROLE_SHOP);
                }
            }
        });
    }

    @OnClick(R.id.btnDone)
    public void onClick() {
        // TODO request register api
        registerRequest();
    }

    private void registerRequest() {
        user.setPhoneNumber(mEdtPhoneNumber.getText().toString());
        user.setPassword(mEdtPasswordRegister.getText().toString());
        user.setName(mEdtNameRegister.getText().toString());
        user.setPlateNumber(mEdtPlateNumber.getText().toString());
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.RegisterUser.USER_PHONE_NUMBER, user.getPhoneNumber());
        userParams.put(APIDefinition.RegisterUser.USER_PASSWORD, user.getPassword());
        userParams.put(APIDefinition.RegisterUser.USER_PASSWORD_CONFIRMATION, user.getPassword());
        userParams.put(APIDefinition.RegisterUser.USER_NAME, user.getName());
        userParams.put(APIDefinition.RegisterUser.USER_ROLE, user.getRole());
        userParams.put(APIDefinition.RegisterUser.USER_PLATE_NUMBER, user.getPlateNumber());

        API.register(userParams, new API.APICallback<APIResponse<RegisterResponse>>() {
            @Override
            public void onResponse(APIResponse<RegisterResponse> response) {
                Toast.makeText(RegisterActivity.this, R.string.register_success,
                               Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.register;
    }

}
