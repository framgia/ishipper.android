package com.framgia.ishipper.net;

import com.framgia.ishipper.common.Config;

/**
 * Created by HungNT on 8/5/16.
 */
public class APIDefinition {

    private static final String DEV_URL = "http://ishipper.herokuapp.com";
    private static final String PROD_URL = "";

    public static final String HEADER_AUTHORIZE = "Authorization";

    public static class RegisterUser {
        public static final String PATH = "/api/sign_up/";
        public static final String USER_PHONE_NUMBER = "user[phone_number]";
        public static final String USER_PASSWORD = "user[password]";
        public static final String USER_PASSWORD_CONFIRMATION = "user[password_confirmation]";
        public static final String USER_PLATE_NUMBER = "user[plate_number]";
        public static final String USER_ROLE = "user[role]";
        public static final String USER_NAME = "user[name]";
    }

    public static class ConfirmationPin {
        public static final String PATH = "/api/confirmation";
    }

    public static class PutUpdateProfile {
        public static final String TAG = "Update Profile";
        public static final String PATH = "/api/users/{id}";

        public static final String PATH_ID = "id";

        public static final String PARAM_PHONE_NUMBER = "user[phone_number]";
        public static final String PARAM_PASSWORD = "user[password]";
        public static final String PARAM_PASSWORD_CONFIRMATION = "user[password_confirmation]";
        public static final String PARAM_NAME = "user[name]";
        public static final String USER_CURRENT_PASSWORD = "user[current_password]";
        public static final String PARAM_PLATE_NUMBER = "user[plate_number]";
        public static final String PARAM_ADDRESS = "user[address]";
    }

    public static class ForgotPassword {
        public static final String TAG = "Forgot Password";
        public static final String PATH = "";
    }

    public static class ChangePassword {
        private static final String TAG = "ChangePassword";
        public static final String PATH = "/api/password";
        public static final String PARAM_PHONE_NUMBER = "user[phone_number]";
        public static final String PARAM_CURRENT_PASSWORD = "user[current_password]";
        public static final String PARAM_PASSWORD = "user[password]";
        public static final String PARAM_PASSWORD_CONFIRMATION = "user[password_confirmation]";
    }

    public static class SignIn {
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
        public static final String USER_LAT_PARAM = "user[latitude]";
        public static final String USER_LNG_PARAM = "user[longitude]";
        public static final String USER_DISTANCE_PARAM = "user[distance]";
    }


    public static class GetInvoiceNearby {
        public static final String PATH = "/api/invoices";
        public static final String USER_LAT_PARAM = "user[latitude]";
        public static final String USER_LNG_PARAM = "user[longitude]";
        public static final String USER_DISTANCE_PARAM = "user[distance]";
    }

    public static class SignOut {
        public static final String PATH = "/api/sign_out";
        public static final String PARAM_PHONE_NUMBER = "user[phone_number]";
        public static final String AUTHENTICATE_TOKEN = "user[authentication_token]";
    }

    public static class PutUpdateInvoice {
        public static final String PATH = "/api/invoices/{id}";

        public static final String PATH_ID = "id";

        public static final String PARAM_NAME = "invoice[name]";
        public static final String PARAM_ADDRESS_START = "invoice[name]";
        public static final String PARAM_LAT_START = "invoice[name]";
        public static final String PARAM_LNG_START = "invoice[name]";
        public static final String PARAM_ADDRESS_FINISH = "invoice[name]";
        public static final String PARAM_LAT_FINISH = "invoice[name]";
        public static final String PARAM_LNG_FINISH = "invoice[name]";
        public static final String PARAM_DELIVERY_TIME = "invoice[name]";
        public static final String PARAM_DISTANCE = "invoice[name]";
        public static final String PARAM_DESCRIPTION = "invoice[name]";
        public static final String PARAM_PRICE = "invoice[name]";
        public static final String PARAM_SHIPPING_PRICE = "invoice[name]";
        public static final String PARAM_STATUS = "invoice[name]";
        public static final String PARAM_WEIGHT = "invoice[name]";
        public static final String PARAM_CUSTOMER_NAME = "invoice[name]";
        public static final String PARAM_CUSTOMER_NUMBER = "invoice[name]";
    }

    public static class InvoiceStatus {
        public static final String STATUS_INIT = "init";
        public static final String STATUS_WAITING = "waiting";
        public static final String STATUS_SHIPPING = "shipping";
        public static final String STATUS_SHIPPED = "shipped";
        public static final String STATUS_FINISH = "finished";
        public static final String STATUS_CANCEL = "cancel";
    }

    public static String getBaseUrl() {
        return Config.IS_DEV ? DEV_URL : PROD_URL;
    }

    public static class CreateInvoice {
        public static final String PATH = "/api/invoices";
        public static final String NAME = "invoice[name]";
        public static final String ADDRESS_START = "invoice[address_start]";
        public static final String LATITUDE_START = "invoice[latitude_start]";
        public static final String LONGITUDE_START = "invoice[longitude_start]";
        public static final String ADDRESS_FINISH = "invoice[address_finish]";
        public static final String LATITUDE_FINISH = "invoice[latitude_finish]";
        public static final String LONGITUDE_FINISH = "invoice[longitude_finish]";
        public static final String DELIVERY_TIME = "invoice[delivery_time]";
        public static final String DISTANCE = "invoice[distance]";
        public static final String DESCRIPTION = "invoice[description]";
        public static final String PRICE = "invoice[price]";
        public static final String SHIPPING_PRICE = "invoice[shipping_price]";
        public static final String STATUS = "invoice[status]";
        public static final String WEIGHT = "invoice[weight]";
        public static final String CUSTOMER_NAME = "invoice[customer_name]";
        public static final String CUSTOMER_NUMBER = "invoice[customer_number]";
    }

    public static class FilterInvoice {
        public static final String PATH = "/api/invoices";
        public static final String MIN_ORDER_PRICE_PARAM = "invoice[min_price]";
        public static final String MAX_ORDER_PRICE_PARAM = "invoice[max_price]";
        public static final String MIN_SHIP_PRICE_PARAM = "invoice[min_shipping_price]";
        public static final String MAX_SHIP_PRICE_PARAM = "invoice[max_shipping_price]";
        public static final String MIN_DISTANCE_PARAM = "invoice[min_distance]";
        public static final String MAX_DISTANCE_PARAM = "invoice[max_distance]";
        public static final String MIN_WEIGHT_PARAM = "invoice[min_weight]";
        public static final String MAX_WEIGHT_PARAM = "invoice[max_weight]";
        public static final String CURRENT_LAT_PARAM = "invoice[latitude]";
        public static final String CURRENT_LONG_PARAM = "invoice[longitude]";
        public static final String RADIUS_PARAM = "invoice[distance]";

    }
}
