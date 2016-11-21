package com.framgia.ishipper.presentation.block;
import com.framgia.ishipper.base.BasePresenter;
import com.framgia.ishipper.base.BaseView;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListUserData;

/**
 * Created by vuduychuong1994 on 11/21/16.
 */

public interface BlackListContract {

    interface View extends BaseView {

        void initViews();

        void showConfirmDialog();

        void showListUser(ListUserData listUserData);

        void insertUser(int index, User blockUser);
    }

    interface Presenter extends BasePresenter {

        void getBlackList(User currentUser);

        void deleteAllBlackList(User currentUser);

        void addUserToBlackList(User currentUser, User blockUser);

        void startSearchUserActivity();
    }
}
