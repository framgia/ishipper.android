package com.framgia.ishipper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;

import com.framgia.ishipper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends ToolbarActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.edt_profile_name) TextInputEditText mEdtProfileName;
    @BindView(R.id.edt_profile_plate) TextInputEditText mEdtProfilePlate;
    @BindView(R.id.edt_profile_email) TextInputEditText mEdtProfileEmail;
    @BindView(R.id.edt_profile_phone) TextInputEditText mEdtProfilePhone;
    @BindView(R.id.edt_profile_address) TextInputEditText mEdtProfileAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.nav_user_name_example;
    }

    @OnClick(R.id.tv_change_password)
    public void onClick() {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }
}
