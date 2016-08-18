package com.framgia.ishipper.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.SignUpData;
import com.framgia.ishipper.ui.fragment.ValidatePinFragment;

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

    User mCurrentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        configUserType();
    }

    private void configUserType() {
        mCurrentUser.setRole(User.ROLE_SHIPPER);
        mRadioGroupUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioShipper) {
                    mLayoutPlate.setVisibility(View.VISIBLE);
                    mTvTitleName.setText(getString(R.string.name));
                    mCurrentUser.setRole(User.ROLE_SHIPPER);
                } else {
                    mLayoutPlate.setVisibility(View.GONE);
                    mTvTitleName.setText(R.string.name_shop);
                    mCurrentUser.setRole(User.ROLE_SHOP);
                }
            }
        });
    }

    @OnClick(R.id.btnDone)
    public void onClick() {
        // TODO request signUp api
        registerRequest();
    }

    private void registerRequest() {
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.show();
        mCurrentUser.setPhoneNumber(mEdtPhoneNumber.getText().toString());
        mCurrentUser.setPassword(mEdtPasswordRegister.getText().toString());
        mCurrentUser.setName(mEdtNameRegister.getText().toString());
        mCurrentUser.setPlateNumber(mEdtPlateNumber.getText().toString());
        Map<String, String> userParams = new HashMap<>();
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PHONE_NUMBER, mCurrentUser.getPhoneNumber());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PASSWORD, mCurrentUser.getPassword());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PASSWORD_CONFIRMATION, mCurrentUser.getPassword());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_NAME, mCurrentUser.getName());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_ROLE, mCurrentUser.getRole());
        userParams.put(APIDefinition.RegisterUser.PARAM_USER_PLATE_NUMBER, mCurrentUser.getPlateNumber());

        API.signUp(userParams, new API.APICallback<APIResponse<SignUpData>>() {
            @Override
            public void onResponse(APIResponse<SignUpData> response) {
                if (progressDialog.isShowing()) progressDialog.hide();
                Config.getInstance().setUserInfo(getBaseContext(), response.getData().getUser());
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, ValidatePinFragment.newInstance(mCurrentUser.getPhoneNumber(),
                                ValidatePinFragment.ACTION_ACTIVATE))
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void onFailure(int code, String message) {
                if (progressDialog.isShowing()) progressDialog.hide();
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
