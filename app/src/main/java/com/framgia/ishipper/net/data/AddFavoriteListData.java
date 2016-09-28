package com.framgia.ishipper.net.data;

import com.framgia.ishipper.model.FavoriteListResponse;
import com.google.gson.annotations.SerializedName;

public class AddFavoriteListData {

    @SerializedName("favorite_list") private FavoriteListResponse mData;

    public FavoriteListResponse getResponse() {
        return mData;
    }
}
