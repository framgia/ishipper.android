package com.framgia.ishipper.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HungNT on 10/21/16.
 */

public class Notification {
    @SerializedName("id") private String mId;
    @SerializedName("content") private String mContent;
    @SerializedName("created_at") private String mTimePost;
    private String avatarUrl;
    @SerializedName("owner_id") private String mOwnerId;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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
}
