package com.framgia.ishipper.model;

/**
 * Created by dinhduc on 23/11/2016.
 */

public class InvoiceHistory {
    private int mStatus;
    private String mTime;
    private String mContent;

    public InvoiceHistory(String time, String content) {
        mTime = time;
        mContent = content;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
