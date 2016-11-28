package com.framgia.ishipper.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HungNT on 10/21/16.
 */

public class Notification {
    @SerializedName("id") private String mId;
    @SerializedName("click_action") private String mAction;
    @SerializedName("invoice_id") private String invoiceId;
    @SerializedName("content") private String mContent;
    @SerializedName("created_at") private String mTimePost;
    @SerializedName("owner_id") private String mOwnerId;
    @SerializedName("read") private boolean mRead;
    private String avatarUrl;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTimePost() {
        return mTimePost;
    }

    public void setTimePost(String timePost) {
        mTimePost = timePost;
    }

    public String getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(String ownerId) {
        mOwnerId = ownerId;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        mRead = read;
    }
}
