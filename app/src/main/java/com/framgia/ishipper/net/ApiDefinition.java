package com.framgia.ishipper.net;

import com.framgia.ishipper.common.Config;

/**
 * Created by HungNT on 8/5/16.
 */
public class APIDefinition {

    private static final String DEV_URL = "192.168.1.21";
    private static final String PROD_URL = "";

    public static class PutUpdateProfile {
        public static final String TAG = "Update Profile";
        public static final String PATH = "/api/users/{id}";

        public static final String PATH_ID = "id";

        public static final String PARAM_PHONE_NUMBER = "user[phone_number]";
        public static final String PARAM_PASSWORD = "user[password]";
        public static final String PARAM_PASSWORD_CONFIRMATION = "user[password_confirmation]";
        public static final String PARAM_ROLE = "user[role]";
        public static final String PARAM_NAME = "user[name]";
        public static final String PARAM_PLATE_NUMBER = "user[plate_number]";
    }

    public static class ForgotPassword {
        public static final String TAG = "Forgot Password";
        public static final String PATH = "";
    }

    public static String getBaseUrl() {
        return Config.IS_DEV ? DEV_URL : PROD_URL;
    }
}
