package com.framgia.ishipper.ui.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.framgia.ishipper.R;
import com.framgia.ishipper.model.ReviewUser;
import com.framgia.ishipper.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dinhduc on 23/08/2016.
 */
public class ReportDialog {
    @BindView(R.id.edt_dialog_report) EditText mEdtDialogReport;
    private AlertDialog mAlertDialog;
    private OnReportListener mListener;
    private ReviewUser mReviewUser = new ReviewUser();

    public ReportDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_report, null);
        ButterKnife.bind(this, view);
        mAlertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();
    }

    @OnClick({R.id.btn_dialog_report_send, R.id.btn_dialog_report_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_report_send:
                if (mListener != null) {
                    mReviewUser.setContent(mEdtDialogReport.getText().toString());
                    mListener.onReportListener(mReviewUser);
                }
                break;
            case R.id.btn_dialog_report_cancel:
                mAlertDialog.cancel();
                break;
        }
    }

    public void show() {
        mAlertDialog.show();
    }

    public void cancel() {
        mAlertDialog.cancel();
    }

    public OnReportListener getListener() {
        return mListener;
    }

    public void setListener(OnReportListener listener) {
        mListener = listener;
    }

    public interface OnReportListener {
        void onReportListener(ReviewUser reviewUser);
    }
}
