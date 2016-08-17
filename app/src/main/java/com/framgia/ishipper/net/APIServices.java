package com.framgia.ishipper.net;

import com.framgia.ishipper.net.data.ChangePasswordData;
import com.framgia.ishipper.net.data.CreateInVoiceData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.InvoiceNearbyData;
import com.framgia.ishipper.net.data.ShipperNearbyData;
import com.framgia.ishipper.net.data.SignInData;
import com.framgia.ishipper.net.data.SignUpData;
import com.framgia.ishipper.net.data.UpdateProfileData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by HungNT on 8/5/16.
 */
public interface APIServices {

    @FormUrlEncoded
    @POST(APIDefinition.RegisterUser.PATH)
    Call<APIResponse<SignUpData>> signUpUser(@FieldMap Map<String, String> userParams);

    @GET(APIDefinition.GetPin.PATH)
    Call<APIResponse<EmptyData>> getConfirmationPin(
            @Query(APIDefinition.GetPin.PARAM_PHONE) String phoneNumber
    );

    @GET(APIDefinition.GetCheckPin.PATH)
    Call<APIResponse<EmptyData>> getCheckPin(
            @Query(APIDefinition.GetCheckPin.PARAM_PHONE) String phoneNumber,
            @Query(APIDefinition.GetCheckPin.PARAM_PIN) String pin
    );

    @GET(APIDefinition.GetUserInformation.PATH)
    Call<APIResponse> getUserInformation(
            @Header("Authorization") String authorization
    );

    @FormUrlEncoded
    @POST(APIDefinition.PutResetPassword.PATH)
    Call<APIResponse<EmptyData>> resetPassword(
            @FieldMap Map<String, String> params
    );

    @FormUrlEncoded
    @PUT(APIDefinition.ChangePassword.PATH)
    Call<APIResponse<ChangePasswordData>> changePassword(
            @FieldMap Map<String, String> params,
            @Header(APIDefinition.ChangePassword.HEADER_AUTHORIZE) String token
    );

    @FormUrlEncoded
    @POST(APIDefinition.SignIn.PATH)
    Call<APIResponse<SignInData>> signIn(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @PUT(APIDefinition.PutUpdateProfile.PATH)
    Call<APIResponse<UpdateProfileData>> putUpdateProfile(
            @FieldMap Map<String, String> params,
            @Path(APIDefinition.PutUpdateProfile.PATH_ID) String userId,
            @Header(APIDefinition.ChangePassword.HEADER_AUTHORIZE) String token
    );

    @FormUrlEncoded
    @PUT(APIDefinition.ConfirmationPin.PATH)
    Call<APIResponse<EmptyData>> confirmationPinInSignup(
            @Field(APIDefinition.GetCheckPin.PARAM_PHONE) String phoneNumber,
            @Field(APIDefinition.GetCheckPin.PARAM_PIN) String pin
    );

    /* Get Shipper nearby */
    @GET(APIDefinition.GetShipperNearby.PATH)
    Call<APIResponse<ShipperNearbyData>> getShipperNearby(
            @Header(APIDefinition.GetShipperNearby.HEADER_AUTHORIZE) String token,
            @QueryMap Map<String, String> userParams);

    /* Get Invoice nearby */
    @GET(APIDefinition.GetInvoiceNearby.PATH)
    Call<APIResponse<InvoiceNearbyData>> getInvoices(
            @Header(APIDefinition.GetInvoiceNearby.HEADER_AUTHORIZE) String token,
            @QueryMap Map<String, String> userParams);

    /* Sign out */
    @DELETE(APIDefinition.SignOut.PATH)
    Call<APIResponse<EmptyData>> signOut(
            @Query(APIDefinition.SignOut.AUTHENTICATE_TOKEN) String token,
            @Query(APIDefinition.SignOut.PARAM_PHONE_NUMBER) String phoneNumber);

    /* Create invoice */
    @FormUrlEncoded
    @POST(APIDefinition.CreateInvoice.PATH)
    Call<APIResponse<CreateInVoiceData>> createInvoice(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @FieldMap Map<String, String> params);

}
