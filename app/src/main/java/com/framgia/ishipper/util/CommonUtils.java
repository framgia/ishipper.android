package com.framgia.ishipper.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.framgia.ishipper.R;
import com.framgia.ishipper.ui.LocationSettingCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by framgia on 18/08/2016.
 */
public class CommonUtils {
    /**
     * Show loading dialog
     **/
    public static Dialog showLoadingDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(context.getString(R.string.dialog_loading_message));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    private static LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Const.LocationRequest.LOCATION_INTERVAL_UPDATE);
        mLocationRequest.setFastestInterval(Const.LocationRequest.LOCATION_FASTEST_INTERVAL_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public static void checkLocationRequestSetting(
        final Activity activity,
        GoogleApiClient googleApiClient,
        final LocationSettingCallback callback) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .setAlwaysShow(true)
            .addLocationRequest(createLocationRequest());
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
            .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        callback.onSuccess();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(activity, Const.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(activity, R.string.all_location_not_available,
                            Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * @param m value on metre
     * @return value on km
     */
    public static float convertMetreToKm(int m) {
        return m / 1000f;
    }

    public static void makePhoneCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
            PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(intent);
    }
}
