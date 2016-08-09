package com.framgia.ishipper.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 8/3/16.
 */
public class ValidatePinFragment extends Fragment {

    private static final String BUNDLE_PHONE = "BUNDLE_PHONE";
    @BindView(R.id.tvDetailText) TextView mTvDetailText;

    private String mPhoneNumber;

    @BindView(R.id.edtPhoneNumber) EditText mEdtPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pin_validate, container, false);
        ButterKnife.bind(this, view);
        mPhoneNumber = getArguments().getString(BUNDLE_PHONE);
        mTvDetailText.setText(getString(R.string.message_phone_validate_sent, mPhoneNumber));

        return view;
    }

    @OnClick({R.id.btnDone, R.id.btnResent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDone:
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.show();
                API.getCheckPin(mPhoneNumber, mEdtPhoneNumber.getText().toString(),
                        new API.APICallback<APIResponse<APIResponse.EmptyResponse>>() {
                            @Override
                            public void onResponse(APIResponse<APIResponse.EmptyResponse> response) {
                                pd.dismiss();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.container, ResetPasswordNewFragment.newInstance(mPhoneNumber,
                                                mEdtPhoneNumber.getText().toString()))
                                        .addToBackStack(null)
                                        .commit();
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

    public static ValidatePinFragment newInstance(String phoneNumber) {

        Bundle args = new Bundle();
        args.putString(BUNDLE_PHONE, phoneNumber);
        ValidatePinFragment fragment = new ValidatePinFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
