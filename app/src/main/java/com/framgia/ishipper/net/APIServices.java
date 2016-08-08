package com.framgia.ishipper.net;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by HungNT on 8/5/16.
 */
public interface APIServices {

    @PUT(APIDefinition.PutUpdateProfile.PATH)
    Call<APIResponse.PutUpdateProfileResponse> updateProfile(
            @Path(APIDefinition.PutUpdateProfile.PATH_ID) String id,
            @FieldMap Map<String, String> params
    );


}
