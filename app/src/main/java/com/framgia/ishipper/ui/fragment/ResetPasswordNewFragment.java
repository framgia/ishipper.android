package com.framgia.ishipper.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 8/3/16.
 */
public class ResetPasswordNewFragment extends Fragment {

    private static final String BUNDLE_PHONE = "BUNDLE_PHONE";
    private static final String BUNDLE_PIN = "BUNDLE_PIN";
    @BindView(R.id.edt_new_password) EditText mEdtNewPassword;
    @BindView(R.id.edt_password_again) EditText mEdtPasswordAgain;

    private String mPhoneNumber;
    private String mPin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_new, container, false);
        ButterKnife.bind(this, view);
        mPhoneNumber = getArguments().getString(BUNDLE_PHONE);
        mPin = getArguments().getString(BUNDLE_PIN);

        return view;
    }

    @OnClick({R.id.btnDone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDone:
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.show();
                HashMap<String, String> params = new HashMap<>();
                params.put(APIDefinition.PutResetPassword.PARAM_PHONE, mPhoneNumber);
                params.put(APIDefinition.PutResetPassword.PARAM_PASSWORD, mEdtNewPassword.getText().toString());
                params.put(APIDefinition.PutResetPassword.PARAM_PASSWORD_CONFIRM, mEdtPasswordAgain.getText().toString());
                params.put(APIDefinition.PutResetPassword.PARAM_PIN, mPin);
                API.postResetPassword(params, new API.APICallback<APIResponse<APIResponse.EmptyResponse>>() {
                    @Override
                    public void onResponse(APIResponse<APIResponse.EmptyResponse> response) {
                        pd.dismiss();
                        getActivity().finish();
                        Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btnResent:
                break;
        }
    }

    public static ResetPasswordNewFragment newInstance(String phoneNumber, String pin) {

        Bundle args = new Bundle();
        args.putString(BUNDLE_PHONE, phoneNumber);
        args.putString(BUNDLE_PIN, pin);
        ResetPasswordNewFragment fragment = new ResetPasswordNewFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
