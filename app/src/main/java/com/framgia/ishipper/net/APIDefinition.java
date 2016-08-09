package com.framgia.ishipper.net;

import com.framgia.ishipper.common.Config;

/**
 * Created by HungNT on 8/5/16.
 */
public class APIDefinition {

    private static final String DEV_URL = "http://192.168.1.118:3001";
    private static final String PROD_URL = "";

    public static class RegisterUser {
        public static final String PATH = "/api/sign_up/";
        public static final String USER_PHONE_NUMBER = "user[phone_number]";
        public static final String USER_PASSWORD = "user[password]";
        public static final String USER_PASSWORD_CONFIRMATION = "user[password_confirmation]";
        public static final String USER_PLATE_NUMBER = "user[plate_number]";
        public static final String USER_ROLE = "user[role]";
        public static final String USER_NAME = "user[name]";
    }

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
    public static class ChangePassword{
        private static final String TAG = "ChangePassword";
        public static final String PATH = "/api/password";
        public static final String HEADER_AUTHORIZE = "Authorization";
        public static final String PARAM_PHONE_NUMBER = "user[phone_number]";
        public static final String PARAM_PASSWORD = "user[password]";
        public static final String PARAM_PASSWORD_CONFIRMATION = "user[password_confirmation]";
    }
    public static class SignIn{
        public static final String PATH = "api/sign_in";
        public static final String PARAM_PHONE_NUMBER = "user[phone_number]";
        public static final String PARAM_PASSWORD = "user[password]";
    }

    public static class GetPin {
        public static final String TAG = "Get Pin";
        public static final String PATH = "/api/confirmation";

        public static final String PARAM_PHONE = "user[phone_number]";
    }

    public static class GetCheckPin {
        public static final String TAG = "Get Pin";
        public static final String PATH = "/api/password";

        public static final String PARAM_PHONE = "user[phone_number]";
        public static final String PARAM_PIN = "user[pin]";
    }

    public static class GetUserInformation {
        public static final String TAG = "Get User Detail";
        public static final String PATH = "/api/users";
    }

    public static class PutResetPassword {
        public static final String TAG = "Put Reset Password";
        public static final String PATH = "/api/password";

        public static final String PARAM_PHONE = "user[phone_number]";
        public static final String PARAM_PASSWORD = "user[password]";
        public static final String PARAM_PASSWORD_CONFIRM = "user[password_confirmation]";
        public static final String PARAM_PIN = "user[pin]";
    }

    public static class GetShipperNearby {

        public static final String PATH = "/api/users";
        public static final String HEADER_AUTHORIZE = "Authorization";
        public static final String USER_LAT_PARAM = "user[latitude]";
        public static final String USER_LNG_PARAM = "user[longitude]";
        public static final String USER_DISTANCE_PARAM = "user[distance]";
    }


    public static class GetInvoiceNearby {
        public static final String PATH = "/api/invoices";
        public static final String HEADER_AUTHORIZE = "Authorization";
        public static final String USER_LAT_PARAM = "user[latitude]";
        public static final String USER_LNG_PARAM = "user[longitude]";
        public static final String USER_DISTANCE_PARAM = "user[distance]";
    }

    public static String getBaseUrl() {
        return Config.IS_DEV ? DEV_URL : PROD_URL;
    }
}
