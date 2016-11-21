package com.framgia.ishipper.presentation.authenication.login;

import android.widget.EditText;

import com.framgia.ishipper.base.BasePresenter;
import com.framgia.ishipper.base.BaseView;

/**
 * Created by framgia on 18/11/2016.
 */

public interface LoginContract {
    interface View extends BaseView{
    }

    interface Presenter extends BasePresenter{
        void login(String prefixPhoneNumber, EditText edtPhoneNumber, EditText edtPassword);

        void startMainActivity();

        void startForgotActivity();

        void startRegisterActivity();
    }
}
