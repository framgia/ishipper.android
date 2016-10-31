package com.framgia.ishipper.service;

import android.content.Intent;

import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.util.Const;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by HungNT on 10/21/16.
 */

public class AppMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent intent = new Intent();
        intent.setAction(Const.Broadcast.NEW_NOTIFICATION_ACTION);
        sendBroadcast(intent);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
}
