package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.server.CallBack;
import com.framgia.ishipper.server.Response;
import com.framgia.ishipper.server.ServerRequestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends ToolbarActivity implements CallBack {

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.edtPasswordRegister) EditText mEdtPasswordRegister;
    @BindView(R.id.edtNameRegister) EditText mEdtNameRegister;
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
        ServerRequestUtils.register(user, this);
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
    public void onSuccess(Response userResponse) {
        Log.d("response", String.valueOf(userResponse.getStatus()) + " ");
        // TODO parse register response
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }
}
