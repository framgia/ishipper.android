package com.framgia.ishipper.model;

/**
 * Created by HungNT on 9/20/16.
 */
public class FbInvoice {

    private String mUserName;
    private String mContent;
    private String mPhoneNumber;
    private String mPostUrl;
    private String mCreatedTime;
    private boolean isPinned;

    public String getPostUrl() {
        return mPostUrl;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getContent() {
        return mContent;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public boolean isPinned() {
        return isPinned;
    }
}
