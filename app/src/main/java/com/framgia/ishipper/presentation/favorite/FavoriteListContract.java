package com.framgia.ishipper.presentation.favorite;
import com.framgia.ishipper.base.BasePresenter;
import com.framgia.ishipper.base.BaseView;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.data.ListUserData;

/**
 * Created by vuduychuong1994 on 11/21/16.
 */

public interface FavoriteListContract {

    interface View extends BaseView {

        void showConfirmDialog();

        void showListUser(ListUserData listUserData);

        void insertUser(int index, User favoriteUser);
    }

    interface Presenter extends BasePresenter {

        void getFavoriteList(User currentUser);

        void deleteAllFavoriteList(User currentUser);

        void addFavoriteUser(User currentUser, User favoriteUser);

        void startSearchUserActivity();
    }
}
