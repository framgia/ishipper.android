package com.framgia.ishipper.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 8/26/16.
 */
public class ReviewUser {
    public static final String TYPE_REPORT = "report";

    @SerializedName("id") private int mId;
    @SerializedName("owner_id") private int mOwnerId;
    @SerializedName("recipient_id") private int mRecipientId;
    @SerializedName("invoice_id") private int mInvoiceId;
    @SerializedName("review_type") private String mReviewType;
    @SerializedName("rating_point") private String mRatingpoint;
    @SerializedName("content") private String mContent;


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(int ownerId) {
        mOwnerId = ownerId;
    }

    public int getRecipientId() {
        return mRecipientId;
    }

    public void setRecipientId(int recipientId) {
        mRecipientId = recipientId;
    }

    public int getInvoiceId() {
        return mInvoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        mInvoiceId = invoiceId;
    }

    public String getReviewType() {
        return mReviewType;
    }

    public void setReviewType(String reviewType) {
        mReviewType = reviewType;
    }

    public String getRatingpoint() {
        return mRatingpoint;
    }

    public void setRatingpoint(String ratingpoint) {
        mRatingpoint = ratingpoint;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
