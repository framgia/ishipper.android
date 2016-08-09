package com.framgia.ishipper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.net.APIResponse;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dinhduc on 08/08/2016.
 */
public class ChangePasswordFragment extends Fragment {
    private static final String TAG = "ChangePasswordFragment";


    @BindView(R.id.edt_new_password) EditText mEdtNewPassword;
    @BindView(R.id.edt_confirm_password) EditText mEdtConfirmPassword;
    @BindView(R.id.btn_change_password) Button mBtnChangePassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_change_password)
    public void onClick() {
        Map<String, String> params = new HashMap<>();
        params.put(APIDefinition.ChangePassword.PARAM_PHONE_NUMBER, "+841234561232");
        params.put(APIDefinition.ChangePassword.PARAM_PASSWORD, mEdtNewPassword.getText().toString());
        params.put(APIDefinition.ChangePassword.PARAM_PASSWORD_CONFIRMATION, mEdtConfirmPassword.getText().toString());
        API.changePassword("EiXhr4PpMnhYP7qPC_FG", params, new API.APICallback<APIResponse<APIResponse.ChangePasswordResponse>>() {

            @Override
            public void onResponse(APIResponse<APIResponse.ChangePasswordResponse> response) {
                Toast.makeText(getContext(), response.getData().getUser().getAuthenticationToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

}
