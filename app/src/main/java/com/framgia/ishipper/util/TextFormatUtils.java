package com.framgia.ishipper.util;
import java.util.Locale;

/**
 * Created by vuduychuong1994 on 8/24/16.
 */
public class TextFormatUtils {

    public static String formatDistance(double distance) {
        return String.format(Locale.getDefault(), "%1$,.1f KM", distance);
    }

    public static String formatPrice(double orderPrice) {
        return String.format(Locale.getDefault(), "%1$,.0f", orderPrice);
    }
}
