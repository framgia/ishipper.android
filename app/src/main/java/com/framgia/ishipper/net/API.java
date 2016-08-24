package com.framgia.ishipper.net;

import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.net.data.ChangePasswordData;
import com.framgia.ishipper.net.data.CreateInVoiceData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.FilterInvoiceData;
import com.framgia.ishipper.net.data.GetUserData;
import com.framgia.ishipper.net.data.InvoiceData;
import com.framgia.ishipper.net.data.ListInvoiceData;
import com.framgia.ishipper.net.data.ListShipperData;
import com.framgia.ishipper.net.data.ShipperNearbyData;
import com.framgia.ishipper.net.data.SignInData;
import com.framgia.ishipper.net.data.SignUpData;
import com.framgia.ishipper.net.data.UpdateProfileData;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HungNT on 8/5/16.
 */
public abstract class API {
    private static final String TAG = "API";
    public static final int LOCAL_ERROR = 1111;
    public static final String SERVER_ERROR = "Server Error";

    private static OkHttpClient loggingClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    private static final APIServices client = new Retrofit.Builder()
            .baseUrl(APIDefinition.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(loggingClient())
            .build()
            .create(APIServices.class);

    // **** Common callback interface ***/
    public interface APICallback<T> {
        void onResponse(T response);

        void onFailure(int code, String message);
    }

    // ** API ******/

    public static void signUp(Map<String, String> userParams,
                              final APICallback<APIResponse<SignUpData>> callback) {
        client.signUpUser(userParams).enqueue(new RetrofitCallback<>(callback));
    }

    public static void confirmationPinInSignUp(String phoneNumber, String pin,
                                               final APICallback<APIResponse<EmptyData>> callback) {
        client.confirmationPinInSignup(phoneNumber, pin)
                .enqueue(new Callback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(Call<APIResponse<EmptyData>> call,
                                           Response<APIResponse<EmptyData>> response) {
                        if (response.body() == null) {
                            Log.d(TAG, SERVER_ERROR);
                        } else if (response.body().isSuccess()) {
                            callback.onResponse(response.body());
                        } else {
                            callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<EmptyData>> call, Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
    }

    public static void changePassword(
            String token,
            Map<String, String> params,
            final APICallback<APIResponse<ChangePasswordData>> callback) {
        client.changePassword(params, token).enqueue(new Callback<APIResponse<ChangePasswordData>>() {
            @Override
            public void onResponse(Call<APIResponse<ChangePasswordData>> call,
                                   Response<APIResponse<ChangePasswordData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, SERVER_ERROR);
                } else if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<ChangePasswordData>> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void signIn(Map<String, String> params,
                              final APICallback<APIResponse<SignInData>> callback) {
        client.signIn(params).enqueue(new RetrofitCallback<>(callback));

    }

    public static void getConfirmationPin(String phoneNumber,
                                          final APICallback<APIResponse<EmptyData>> callback) {
        client.getConfirmationPin(phoneNumber).enqueue(new Callback<APIResponse<EmptyData>>() {
            @Override
            public void onResponse(Call<APIResponse<EmptyData>> call,
                                   Response<APIResponse<EmptyData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, SERVER_ERROR);
                } else if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<EmptyData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void getCheckPin(String phoneNumber, String pin,
                                   final APICallback<APIResponse<EmptyData>> callback) {
        client.getCheckPin(phoneNumber, pin).enqueue(new Callback<APIResponse<EmptyData>>() {
            @Override
            public void onResponse(Call<APIResponse<EmptyData>> call,
                                   Response<APIResponse<EmptyData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, SERVER_ERROR);
                } else if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<EmptyData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void postResetPassword(HashMap<String, String> params,
                                         final APICallback<APIResponse<EmptyData>> callback) {
        client.resetPassword(params).enqueue(new Callback<APIResponse<EmptyData>>() {
            @Override
            public void onResponse(Call<APIResponse<EmptyData>> call,
                                   Response<APIResponse<EmptyData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, SERVER_ERROR);
                } else if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<EmptyData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    /* Get shipper nearby */
    public static void getShipperNearby(String token,
                                        Map<String, String> userParams,
                                        final APICallback<APIResponse<ShipperNearbyData>> callback) {
        client.getShipperNearby(token, userParams).enqueue(new RetrofitCallback<>(callback));
    }

    /* Get Invoice nearby */
    public static void getInvoiceNearby(String token, Map<String, String> userParams,
                                        final APICallback<APIResponse<ListInvoiceData>> callback) {
        client.getInvoiceNearBy(token, userParams).enqueue(new RetrofitCallback<>(callback));
    }

    public static void putUpdateProfile(HashMap<String, String> params,
                                        final APICallback<APIResponse<UpdateProfileData>> callback) {
        client.putUpdateProfile(
                params,
                Config.getInstance().getUserInfo(null).getId(),
                Config.getInstance().getUserInfo(null).getAuthenticationToken()
        ).enqueue(new Callback<APIResponse<UpdateProfileData>>() {
                      @Override
                      public void onResponse(Call<APIResponse<UpdateProfileData>> call,
                                             Response<APIResponse<UpdateProfileData>> response) {
                          if (response.isSuccessful()) {
                              if (response.body().isSuccess()) {
                                  callback.onResponse(response.body());
                              } else {
                                  callback.onFailure(response.body().getCode(), response.body().getMessage());
                              }
                          } else {
                              callback.onFailure(response.code(), response.message());
                          }
                      }

                      @Override
                      public void onFailure(Call<APIResponse<UpdateProfileData>> call, Throwable t) {
                          callback.onFailure(LOCAL_ERROR, t.getMessage());
                      }
                  }

        );
    }

    /* Sign out */
    public static void signOut(String token,
                               String phoneNumber,
                               final APICallback<APIResponse<EmptyData>> callback) {
        client.signOut(token, phoneNumber).enqueue(new Callback<APIResponse<EmptyData>>() {
            @Override
            public void onResponse(Call<APIResponse<EmptyData>> call,
                                   Response<APIResponse<EmptyData>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onFailure(response.body().getCode(), response.body().getMessage());
                    }
                } else {
                    callback.onFailure(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<EmptyData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    /* Create Invoice */
    public static void createInvoice(String token,
                                     Map<String, String> params,
                                     final APICallback<APIResponse<CreateInVoiceData>> callback) {
        client.createInvoice(token, params).enqueue(
                new Callback<APIResponse<CreateInVoiceData>>() {
                    @Override
                    public void onResponse(Call<APIResponse<CreateInVoiceData>> call,
                                           Response<APIResponse<CreateInVoiceData>> response) {
                        if (response.isSuccessful()) {
                            if (response.body().isSuccess()) {
                                callback.onResponse(response.body());
                            } else {
                                callback.onFailure(response.body().getCode(), response.body().getMessage());
                            }
                        } else {
                            callback.onFailure(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<CreateInVoiceData>> call, Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
    }

    public static void putUpdateInvoice(HashMap<String, String> params, String invoiceId,
                                        final APICallback<APIResponse<InvoiceData>> callback) {
        client.putUpdateInvoice(
                params,
                invoiceId,
                Config.getInstance().getUserInfo(null).getAuthenticationToken()
        ).enqueue(new Callback<APIResponse<InvoiceData>>() {
                      @Override
                      public void onResponse(Call<APIResponse<InvoiceData>> call,
                                             Response<APIResponse<InvoiceData>> response) {
                          if (response.isSuccessful()) {
                              if (response.body().isSuccess()) {
                                  callback.onResponse(response.body());
                              } else {
                                  callback.onFailure(response.body().getCode(), response.body().getMessage());
                              }
                          } else {
                              callback.onFailure(response.code(), response.message());
                          }
                      }

                      @Override
                      public void onFailure(Call<APIResponse<InvoiceData>> call, Throwable t) {
                          callback.onFailure(LOCAL_ERROR, t.getMessage());
                      }
                  }
        );
    }

    /**
     * @param status
     * @param callback
     */
    public static void putUpdateInvoiceStatus(String userType, String invoiceId, String token, String status,
                                              final APICallback<APIResponse<InvoiceData>> callback) {

        client.putUpdateInvoiceStatus(userType, invoiceId, token, status).enqueue(
                new Callback<APIResponse<InvoiceData>>() {
                    @Override
                    public void onResponse(Call<APIResponse<InvoiceData>> call, Response<APIResponse<InvoiceData>> response) {
                        if (response.isSuccessful()) {
                            if (response.body().isSuccess()) {
                                callback.onResponse(response.body());
                            } else {
                                callback.onFailure(response.body().getCode(), response.body().getMessage());
                            }
                        } else {
                            callback.onFailure(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<InvoiceData>> call, Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
    }

    /* Filter Invoice */
    public static void filterInvoice(String token,
                                     Map<String, String> params,
                                     final APICallback<APIResponse<FilterInvoiceData>> callback) {
        client.filterInvoice(token, params).enqueue(new Callback<APIResponse<FilterInvoiceData>>() {
            @Override
            public void onResponse(
                    Call<APIResponse<FilterInvoiceData>> call,
                    Response<APIResponse<FilterInvoiceData>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onFailure(response.body().getCode(), response.body().getMessage());
                    }
                } else {
                    callback.onFailure(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<FilterInvoiceData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    /* Get List Shipper Invoices */
    public static void getListShipperInvoices(String token,
                                              String status,
                                              final APICallback<APIResponse<ListInvoiceData>> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put(APIDefinition.GetListInvoice.PARAM_STATUS, status);
        getInvoice(User.ROLE_SHIPPER, token, params, callback);
    }

    /* Get Invoice */
    public static void getInvoice(String userType,
                                  String token,
                                  Map<String, String> params,
                                  final APICallback<APIResponse<ListInvoiceData>> callback) {
        client.getListInvoice(userType, token, params).enqueue(new RetrofitCallback<>(callback));
    }

    public static void getUser(
            String token,
            String userId,
            final APICallback<APIResponse<GetUserData>> callback) {
        client.getUser(token, userId).enqueue(new Callback<APIResponse<GetUserData>>() {
            @Override
            public void onResponse(
                    Call<APIResponse<GetUserData>> call,
                    Response<APIResponse<GetUserData>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onFailure(response.body().getCode(), response.body().getMessage());
                    }
                } else {
                    callback.onFailure(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<GetUserData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    /* Get List Shippers Received */
    public static void getListShipperReceived(String token,
                                              String invoiceId,
                                              final APICallback<APIResponse<ListShipperData>> callback) {
        client.getListShipperReceived(token, invoiceId).enqueue(new Callback<APIResponse<ListShipperData>>() {
            @Override
            public void onResponse(Call<APIResponse<ListShipperData>> call, Response<APIResponse<ListShipperData>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onFailure(response.body().getCode(), response.body().getMessage());
                    }
                } else {
                    callback.onFailure(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<ListShipperData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());

            }
        });
    }
}
