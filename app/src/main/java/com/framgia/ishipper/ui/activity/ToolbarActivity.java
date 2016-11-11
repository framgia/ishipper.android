package com.framgia.ishipper.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.ui.listener.SocketCallback;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 8/3/16.
 */
public abstract class ToolbarActivity extends AppCompatActivity {
    private static final String TAG = "ToolbarActivity";

    abstract Toolbar getToolbar();
    abstract int getActivityTitle();
    abstract int getLayoutId();

    protected WebSocket mWebSocket;
    protected Thread mThread;

    protected void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getActivityTitle());
    }

    protected void connectWebSocket(SocketCallback callback) {
        mThread = new Thread(new WebSocketClient(callback));
        mThread.start();
    }

    protected void disconnectWebSocket() {
        mWebSocket.disconnect();
        mThread.interrupt();
    }

    protected WebSocket getWebSocket() {
        return mWebSocket;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setToolbar(getToolbar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    class WebSocketClient implements Runnable {
        private SocketCallback mCallback;

        public WebSocketClient(SocketCallback callback) {
            mCallback = callback;
        }

        @Override
        public void run() {
            setUpSocketWebClient();
        }

        private void setUpSocketWebClient() {
            WebSocketFactory factory = new WebSocketFactory()
                    .setConnectionTimeout(APIDefinition.WebSockets.TIMEOUT_DEFAULT);
            try {
                User user = Config.getInstance().getUserInfo(ToolbarActivity.this);
                mWebSocket = factory.createSocket(APIDefinition.WebSockets.WEBSOCKETS_URL)
                        .setUserInfo(user.getAuthenticationToken());
                mWebSocket.addHeader(APIDefinition.WebSockets.PARAM_PHONE_NUMBER, user.getPhoneNumber());
                mWebSocket.setPingInterval(APIDefinition.WebSockets.INTERVAL_DEFAULT);
                mWebSocket.connect();
                mWebSocket.addListener(new WebSocketAdapter(){
                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        Log.d(TAG, text);
                        if (mCallback != null) {
                            mCallback.onCallback(websocket, text);
                        }
                    }
                });
            } catch (IOException | WebSocketException e) {
                e.printStackTrace();
            }

        }
    }
}
