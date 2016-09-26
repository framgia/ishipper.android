package com.framgia.ishipper.net.data;

import com.framgia.ishipper.model.BlackListResponse;
import com.google.gson.annotations.SerializedName;

public class AddBlacklistData {

    @SerializedName("black_list") private BlackListResponse mData;

    public BlackListResponse getResponse() {
        return mData;
    }
}
