package com.framgia.ishipper.net;

import android.widget.Toast;
import com.framgia.ishipper.common.Config;
import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.net.data.ChangePasswordData;
import com.framgia.ishipper.net.data.EmptyData;
import com.framgia.ishipper.net.data.InvoiceNearbyData;
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
        client.signUpUser(userParams).enqueue(new Callback<APIResponse<SignUpData>>() {
            @Override
            public void onResponse(Call<APIResponse<SignUpData>> call,
                                   Response<APIResponse<SignUpData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, "Server Error");
                } else if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<SignUpData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void confirmationPinInSignUp(String phoneNumber, String pin,
                                               final APICallback<APIResponse<EmptyData>> callback) {
        client.confirmationPinInSignup(phoneNumber, pin)
                .enqueue(new Callback<APIResponse<EmptyData>>() {
                    @Override
                    public void onResponse(Call<APIResponse<EmptyData>> call,
                                           Response<APIResponse<EmptyData>> response) {
                        if (response.body() == null) {
                            Log.d(TAG, "Server Error");
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
                    Log.d(TAG, "Server Error");
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
        client.signIn(params).enqueue(new Callback<APIResponse<SignInData>>() {
            @Override
            public void onResponse(Call<APIResponse<SignInData>> call,
                                   Response<APIResponse<SignInData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, "Server Error");
                } else if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<SignInData>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });

    }

    public static void getConfirmationPin(String phoneNumber,
                                          final APICallback<APIResponse<EmptyData>> callback) {
        client.getConfirmationPin(phoneNumber).enqueue(new Callback<APIResponse<EmptyData>>() {
            @Override
            public void onResponse(Call<APIResponse<EmptyData>> call,
                                   Response<APIResponse<EmptyData>> response) {
                if (response.body() == null) {
                    Log.d(TAG, "Server Error");
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
                    Log.d(TAG, "Server Error");
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
                    Log.d(TAG, "Server Error");
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
        client.getShipperNearby(token, userParams).enqueue(
                new Callback<APIResponse<ShipperNearbyData>>() {
                    @Override
                    public void onResponse(Call<APIResponse<ShipperNearbyData>> call,
                                           Response<APIResponse<ShipperNearbyData>> response) {

                        if (response.body().isSuccess()) {
                            callback.onResponse(response.body());
                        } else {
                            callback.onFailure(response.body().getCode(),
                                    response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<ShipperNearbyData>> call,
                                          Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
    }

    /* Get Invoice nearby */
    public static void getInvoiceNearby(String token, Map<String, String> userParams,
                                        final APICallback<APIResponse<InvoiceNearbyData>> callback) {
        client.getInvoices(token, userParams).enqueue(
                new Callback<APIResponse<InvoiceNearbyData>>() {
                    @Override
                    public void onResponse(Call<APIResponse<InvoiceNearbyData>> call,
                                           Response<APIResponse<InvoiceNearbyData>> response) {
                        if (response.body().isSuccess()) {
                            callback.onResponse(response.body());
                        } else {
                            callback.onFailure(response.body().getCode(), response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<InvoiceNearbyData>> call, Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
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

}
