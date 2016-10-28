package com.framgia.ishipper.ui.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.Notification;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.API;
import com.framgia.ishipper.net.APIResponse;
import com.framgia.ishipper.net.data.ListNotificationData;
import com.framgia.ishipper.ui.adapter.NotificationAdapter;
import com.framgia.ishipper.util.Const;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends ToolbarActivity {

    @BindView(R.id.rvListNotification) RecyclerView rvListNotification;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    private NotificationAdapter mAdapter;
    private List<Notification> mNotificationList;
    private User mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        mCurrentUser = Config.getInstance().getUserInfo(this);
        if (mCurrentUser != null) {
            return;
        }
        API.getAllNotification(
                mCurrentUser.getAuthenticationToken(),
                mCurrentUser.getRole(),
                1,
                Const.Setting.PER_PAGE,
                new API.APICallback<APIResponse<ListNotificationData>>() {
                    @Override
                    public void onResponse(APIResponse<ListNotificationData> response) {
                        mNotificationList = response.getData().getNotifications();
                        mAdapter = new NotificationAdapter(getBaseContext(), mNotificationList);
                        rvListNotification.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                LinearLayoutManager.VERTICAL, false));
                        rvListNotification.setAdapter(mAdapter);
                        Toast.makeText(getBaseContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    int getActivityTitle() {
        return R.string.title_activity_notification;
    }
}
