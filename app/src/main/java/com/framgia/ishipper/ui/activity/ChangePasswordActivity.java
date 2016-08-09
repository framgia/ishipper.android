package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.fragment.ChangePasswordFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends ToolbarActivity {

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;
    @BindView(R.id.btnDone) TextView mBtnDone;
    @BindView(R.id.btnResent) TextView mBtnResent;
    @BindView(R.id.container_reset_password) FrameLayout mContainerResetPassword;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
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

    @OnClick({R.id.btnDone, R.id.btnResent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDone:
                replaceFragment(R.id.container_reset_password, new ChangePasswordFragment());
                break;
            case R.id.btnResent:
                break;
        }
    }

    private void replaceFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}
