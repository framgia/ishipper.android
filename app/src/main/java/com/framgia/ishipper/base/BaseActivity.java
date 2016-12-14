package com.framgia.ishipper.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.APIDefinition;
import com.framgia.ishipper.ui.listener.SocketCallback;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by HungNT on 8/3/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    protected WebSocket mWebSocket;
    protected Thread mThread;
    protected ProgressDialog mDialog;

    protected boolean allowInitViews = true;

    public abstract void initViews();

    public abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        if (allowInitViews) initViews();
    }

    protected void connectWebSocket(SocketCallback callback) {
        mThread = new Thread(new WebSocketClient(callback));
        mThread.start();
    }

    public void showUserMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showUserMessage(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void showDialog() {
        if (mDialog == null) initDialog();
        if (mDialog != null && !mDialog.isShowing()) mDialog.show();
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    private void initDialog() {
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(getString(R.string.dialog_loading_message));
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
    }

    protected void disconnectWebSocket() {
        mWebSocket.disconnect();
        mThread.interrupt();
    }

    protected WebSocket getWebSocket() {
        return mWebSocket;
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
                User user = Config.getInstance().getUserInfo(BaseActivity.this);
                mWebSocket = factory.createSocket(APIDefinition.WebSockets.WEBSOCKETS_URL)
                        .setUserInfo(user.getAuthenticationToken());
                mWebSocket.addHeader(APIDefinition.WebSockets.PARAM_PHONE_NUMBER, user.getPhoneNumber());
                mWebSocket.setPingInterval(APIDefinition.WebSockets.INTERVAL_DEFAULT);
                mWebSocket.connect();
                mWebSocket.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        Log.d(TAG, text);
                        if (mCallback != null) {
                            mCallback.onCallback(websocket, text);
                        }
                    }

                    @Override
                    public void onConnected(
                            WebSocket websocket, Map<String, List<String>> headers)
                            throws Exception {
                        Log.d(TAG, "socket connected");
                    }

                    @Override
                    public void onDisconnected(
                            WebSocket websocket, WebSocketFrame serverCloseFrame,
                            WebSocketFrame clientCloseFrame, boolean closedByServer)
                            throws Exception {
                        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame,
                                             closedByServer);
                        if (closedByServer) {
                            Log.d(TAG, "socket disconnected by server");
                            if (mWebSocket != null) mWebSocket.connect();
                        } else {
                            Log.d(TAG, "socket disconnected by client");
                        }
                    }
                });
            } catch (IOException | WebSocketException e) {
                e.printStackTrace();
            }

        }
    }
}
