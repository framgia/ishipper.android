package com.framgia.ishipper.ui.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.framgia.ishipper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dinhduc on 24/08/2016.
 */
public class ReviewDialog {
    @BindView(R.id.edt_dialog_review) EditText mEdtDialogReview;
    private AlertDialog mDialog;

    public ReviewDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_review, null);
        ButterKnife.bind(this, view);
        mDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();
    }

    @OnClick({R.id.btn_dialog_review_send, R.id.btn_dialog_review_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_review_send:

                break;
            case R.id.btn_dialog_review_cancel:
                mDialog.cancel();
                break;
        }
    }

    public void show() {
        mDialog.show();
    }

    public void cancel() {
        mDialog.cancel();
    }
}
