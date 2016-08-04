package com.framgia.ishipper.server;
/**
 * Created by vuduychuong1994 on 8/4/16.
 */
public interface CallBack {

    void onSuccess(Response userResponse);

    void onFailure(Throwable t);
}
