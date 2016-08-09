package com.framgia.ishipper.net;

import com.framgia.ishipper.server.RegisterResponse;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Header;
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

    @FormUrlEncoded
    @POST(APIDefinition.RegisterUser.PATH)
    Call<APIResponse<RegisterResponse>> signupUser(@FieldMap Map<String, String> userParams);

    @FormUrlEncoded
    @PUT(APIDefinition.ChangePassword.PATH)
    Call<APIResponse<APIResponse.ChangePasswordResponse>> changePassword(
            @FieldMap Map<String, String> params,
            @Header(APIDefinition.ChangePassword.HEADER_AUTHORIZE) String token
    );
}
