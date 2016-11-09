package com.framgia.ishipper.ui.listener;
import com.neovisionaries.ws.client.WebSocket;

/**
 * Created by vuduychuong1994 on 11/8/16.
 */

public interface SocketCallback {
    void onCallback (WebSocket websocket, String text);
}
