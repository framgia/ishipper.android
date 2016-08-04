package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.fragment.ValidatePhoneFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 8/3/16.
 */
public class ForgetPasswordActivity extends ToolbarActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);

    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.forgot_pass;
    }

    @OnClick(R.id.btnDone)
    public void onClick() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ValidatePhoneFragment())
                .addToBackStack(null)
                .commit();
    }
}
