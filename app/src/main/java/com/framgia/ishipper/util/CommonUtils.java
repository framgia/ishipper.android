package com.framgia.ishipper.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.framgia.ishipper.R;

/**
 * Created by framgia on 18/08/2016.
 */
public class CommonUtils {
    public static Dialog showLoadingDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(context.getString(R.string.dialog_loading_message));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
}
