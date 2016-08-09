package com.framgia.ishipper.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.ui.fragment.ValidatePinFragment;
import com.framgia.ishipper.util.Const;

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

        if (mEdtPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(ForgetPasswordActivity.this, R.string.error_empty_string, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.show();

        final String phoneNumber = Const.VIETNAM_PREFIX + mEdtPhoneNumber.getText().toString();
        API.getConfirmationPin(phoneNumber,
                new API.APICallback<APIResponse<APIResponse.EmptyResponse>>() {
                    @Override
                    public void onResponse(APIResponse<APIResponse.EmptyResponse> response) {
                        pd.dismiss();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.container, ValidatePinFragment.newInstance(phoneNumber))
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        pd.dismiss();
                        Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
