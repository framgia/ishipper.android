package com.framgia.ishipper.net;

import com.framgia.ishipper.net.data.ChangePasswordData;
import com.framgia.ishipper.net.data.CreateInVoiceData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.FilterInvoiceData;
import com.framgia.ishipper.net.data.GetUserData;
import com.framgia.ishipper.net.data.InvoiceData;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.net.data.ListReviewData;
import com.framgia.ishipper.net.data.ListShipperData;
import com.framgia.ishipper.net.data.ReportUserData;
import com.framgia.ishipper.net.data.ShipperNearbyData;
import com.framgia.ishipper.net.data.ShowInvoiceData;
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
            @Header(APIDefinition.HEADER_AUTHORIZE) String authorization
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
            @Header(APIDefinition.HEADER_AUTHORIZE) String token
    );

    @FormUrlEncoded
    @POST(APIDefinition.SignIn.PATH)
    Call<APIResponse<SignInData>> signIn(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @PUT(APIDefinition.PutUpdateProfile.PATH)
    Call<APIResponse<UpdateProfileData>> putUpdateProfile(
            @FieldMap Map<String, String> params,
            @Path(APIDefinition.PutUpdateProfile.PATH_ID) String userId,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token
    );

    @FormUrlEncoded
    @PUT(APIDefinition.ConfirmationPin.PATH)
    Call<APIResponse<EmptyData>> confirmationPinInSignUp(
            @Field(APIDefinition.GetCheckPin.PARAM_PHONE) String phoneNumber,
            @Field(APIDefinition.GetCheckPin.PARAM_PIN) String pin
    );

    @GET(APIDefinition.GetShipperNearby.PATH)
    Call<APIResponse<ShipperNearbyData>> getShipperNearby(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @QueryMap Map<String, String> userParams);

    @GET(APIDefinition.GetInvoiceNearby.PATH)
    Call<APIResponse<ListInvoiceData>> getInvoiceNearBy(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @QueryMap Map<String, String> userParams);

    @DELETE(APIDefinition.SignOut.PATH)
    Call<APIResponse<EmptyData>> signOut(
            @Query(APIDefinition.SignOut.AUTHENTICATE_TOKEN) String token,
            @Query(APIDefinition.SignOut.PARAM_PHONE_NUMBER) String phoneNumber);

    @FormUrlEncoded
    @POST(APIDefinition.CreateInvoice.PATH)
    Call<APIResponse<CreateInVoiceData>> createInvoice(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @PUT(APIDefinition.PutUpdateInvoice.PATH)
    Call<APIResponse<InvoiceData>> putUpdateInvoice(
            @FieldMap Map<String, String> params,
            @Path(APIDefinition.PutUpdateInvoice.PATH_ID) String invoiceId,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token
    );

    @GET(APIDefinition.FilterInvoice.PATH)
    Call<APIResponse<FilterInvoiceData>> filterInvoice(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @QueryMap Map<String, String> params);

    @GET(APIDefinition.GetListInvoice.PATH)
    Call<APIResponse<ListInvoiceData>> getListInvoice(
            @Path(APIDefinition.GetListInvoice.PARAM_USER_TYPE) String userType,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @PUT(APIDefinition.PutUpdateInvoiceStatus.PATH)
    Call<APIResponse<InvoiceData>> putUpdateInvoiceStatus(
            @Path(APIDefinition.PutUpdateInvoiceStatus.PARAM_USER_TYPE) String userType,
            @Path(APIDefinition.PutUpdateInvoiceStatus.PARAM_ID) String id,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @Field(APIDefinition.PutUpdateInvoiceStatus.PARAM_STATUS) String status);

    @GET(APIDefinition.GetUser.PATH)
    Call<APIResponse<GetUserData>> getUser(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @Path(APIDefinition.GetUser.PARAM_USER_ID) String id);

    @GET(APIDefinition.GetListShipperReceived.PATH)
    Call<APIResponse<ListShipperData>> getListShipperReceived(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @Query(APIDefinition.GetListShipperReceived.PARAM_INVOICE_ID) String id);

    @FormUrlEncoded
    @PUT(APIDefinition.PutShopReceiveShipper.PATH)
    Call<APIResponse<EmptyData>> putShopReceiveShipper(
            @Field(APIDefinition.PutShopReceiveShipper.PARAMS_STATUS) String status,
            @Path(APIDefinition.PutShopReceiveShipper.PARAM_USER_INVOICE_ID) String userInvoiceId,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token
    );

    @FormUrlEncoded
    @POST(APIDefinition.PostShipperReceiveInvoice.PATH)
    Call<APIResponse<EmptyData>> postShipperReceiveInvoice(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @Field(APIDefinition.PostShipperReceiveInvoice.PARAMS_INVOICE_ID) String status
    );

    @GET(APIDefinition.ShowInvoice.PATH)
    Call<APIResponse<ShowInvoiceData>> getInvoiceDetail(
            @Path(APIDefinition.PutUpdateInvoiceStatus.PARAM_USER_TYPE) String userType,
            @Path(APIDefinition.PutUpdateInvoiceStatus.PARAM_ID) String id,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token);

    @FormUrlEncoded
    @POST(APIDefinition.PostRating.PATH)
    Call<APIResponse<EmptyData>> postRating(
            @Path(APIDefinition.PutUpdateInvoiceStatus.PARAM_USER_TYPE) String userType,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(APIDefinition.ReportUser.PATH)
    Call<APIResponse<ReportUserData>> reportUser(
            @Path(APIDefinition.ReportUser.PARAM_USER_TYPE) String userType,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token,
            @FieldMap Map<String, String> params);

    @GET(APIDefinition.GetSearchInvoice.PATH)
    Call<APIResponse<ListInvoiceData>> getSearchInvoice(
            @Path(APIDefinition.GetSearchInvoice.PARAM_STATUS) String status,
            @Path(APIDefinition.GetSearchInvoice.PARAM_QUERY) String query,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token);

    @GET(APIDefinition.ShowBlackListShipper.PATH)
    Call<APIResponse<ListShipperData>> getBlackListShipper(
            @Header(APIDefinition.HEADER_AUTHORIZE) String token);

    @GET(APIDefinition.GetListReview.PATH)
    Call<APIResponse<ListReviewData>> getListReview(
            @Path(APIDefinition.GetListReview.PARAMS_USER_ID) String userId,
            @Header(APIDefinition.HEADER_AUTHORIZE) String token);
}
