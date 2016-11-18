package com.framgia.ishipper.presentation.authenication.register;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.base.BaseActivity;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.SignUpData;
import com.framgia.ishipper.ui.fragment.ValidatePinFragment;
import com.framgia.ishipper.util.CommonUtils;
import com.framgia.ishipper.util.InputValidate;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.edtPasswordRegister) EditText mEdtPasswordRegister;
    @BindView(R.id.edtPasswordConfirm) EditText mEdtPasswordConfirm;
    @BindView(R.id.edtNameRegister) EditText mEdtNameRegister;
    @BindView(R.id.edtPlateNumber) EditText mEdtPlateNumber;
    @BindView(R.id.radioGroupUserType) RadioGroup mRadioGroupUserType;
    @BindView(R.id.layoutPlate) LinearLayout mLayoutPlate;
    @BindView(R.id.tvTitleName) TextView mTvTitleName;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.spnRegisterPrefix) Spinner mSpnPrefixPhoneNumber;

    private User mCurrentUser = new User();
    private String mPrefixPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configUserType();
        setUpSpinner();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.prefix_phone_number,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnPrefixPhoneNumber.setAdapter(adapter);
        mSpnPrefixPhoneNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPrefixPhoneNumber = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mPrefixPhoneNumber = "";
            }
        });
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
        registerRequest();
    }

    private void registerRequest() {

        if (mCurrentUser.getRole().equals(User.ROLE_SHIPPER)
                && !InputValidate.notEmpty(mEdtPlateNumber, this)) {
            return;
        }

        if (!InputValidate.notEmpty(mEdtPhoneNumber, this)
                || !InputValidate.notEmpty(mEdtNameRegister, this)
                || !InputValidate.confirmPassword(mEdtPasswordRegister, mEdtPasswordConfirm, this)) {
            return;
        }

        mCurrentUser.setPhoneNumber(mPrefixPhoneNumber + mEdtPhoneNumber.getText().toString());
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

        final Dialog loadingDialog = CommonUtils.showLoadingDialog(this);
        API.signUp(userParams, new API.APICallback<APIResponse<SignUpData>>() {
            @Override
            public void onResponse(APIResponse<SignUpData> response) {
                loadingDialog.dismiss();
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
                loadingDialog.dismiss();
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

    @Override
    int getLayoutId() {
        return R.layout.activity_register;
    }

}
