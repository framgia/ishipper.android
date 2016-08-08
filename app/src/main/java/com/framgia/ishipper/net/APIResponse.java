package com.framgia.ishipper.net;

import com.framgia.ishipper.model.User;
import com.google.gson.JsonObject;

/**
 * Created by HungNT on 8/5/16.
 */

interface APIError {
    /* Common */
    public int BAD_REQUEST = 400;
    public int DATA_NOT_FOUND = 404;

    /* Security */
    public int REQUEST_NOT_AUTHORIZED = 401;

    /* Local Error */
    public int LOCAL_ERROR = 1111;

}

public abstract class APIResponse {
    public boolean success;
    public int code;
    public String message;
    public JsonObject data;

    public APIResponse() {
        success = code == 1;
    }

    public static class PutUpdateProfileResponse extends APIResponse {
        public User user;

        public PutUpdateProfileResponse() {
            if (success) {
                user = API.sConverter.fromJson(data.toString(), User.class);
            } else {
                user = null;
            }
        }
    }
}
