package com.framgia.ishipper.util;

/**
 * Created by vuduychuong1994 on 8/3/16.
 */
public class Const {

    public static final int REQUEST_CHECK_SETTINGS = 2;
    public static final long TIME_DELAY_EXIT = 2000;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int PIN_LENGTH = 8;
    public static String VIETNAM_PREFIX = "+84";

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public class Storage {

        public static final String KEY_USER_INFO = "UserStorageKey";
        public static final String KEY_IS_LOGIN = "IsLoginKey";
    }

    public class LocationRequest {
        public static final int LOCATION_INTERVAL_UPDATE = 10000;
        public static final int LOCATION_FASTEST_INTERVAL_UPDATE = 5000;
    }

    public class MapPadding {
        public static final int TOP_PADDING = 150;
        public static final int RIGHT_PADDING = 0;
        public static final int BOTTOM_PADDING = 0;
        public static final int LEFT_PADDING = 0;
    }
}
