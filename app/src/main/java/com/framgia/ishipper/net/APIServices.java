package com.framgia.ishipper.net;

import com.framgia.ishipper.server.SignUpResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by HungNT on 8/5/16.
 */
public interface APIServices {

    @FormUrlEncoded
    @POST(APIDefinition.RegisterUser.PATH)
    Call<APIResponse<SignUpResponse>> signUpUser(@FieldMap Map<String, String> userParams);

    @GET(APIDefinition.GetPin.PATH)
    Call<APIResponse<APIResponse.EmptyResponse>> getConfirmationPin(
            @Query(APIDefinition.GetPin.PARAM_PHONE) String phoneNumber
    );

    @GET(APIDefinition.GetCheckPin.PATH)
    Call<APIResponse<APIResponse.EmptyResponse>> getCheckPin(
            @Query(APIDefinition.GetCheckPin.PARAM_PHONE) String phoneNumber,
            @Query(APIDefinition.GetCheckPin.PARAM_PIN) String pin
    );

    @GET(APIDefinition.GetUserInformation.PATH)
    Call<APIResponse> getUserInformation(
            @Header("Authorization") String authorization
    );

    @FormUrlEncoded
    @POST(APIDefinition.PutResetPassword.PATH)
    Call<APIResponse<APIResponse.EmptyResponse>> resetPassword(
            @FieldMap Map<String, String> params
    );

    @FormUrlEncoded
    @PUT(APIDefinition.ChangePassword.PATH)
    Call<APIResponse<APIResponse.ChangePasswordResponse>> changePassword(
            @FieldMap Map<String, String> params,
            @Header(APIDefinition.ChangePassword.HEADER_AUTHORIZE) String token
    );

    @FormUrlEncoded
    @POST(APIDefinition.SignIn.PATH)
    Call<APIResponse<APIResponse.SignInResponse>> signIn( @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @PUT(APIDefinition.ConfirmationPin.PATH)
    Call<APIResponse<APIResponse.EmptyResponse>> confirmationPinInSignup(
            @Field(APIDefinition.GetCheckPin.PARAM_PHONE) String phoneNumber,
            @Field(APIDefinition.GetCheckPin.PARAM_PIN) String pin
    );
}
