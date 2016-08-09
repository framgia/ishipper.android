package com.framgia.ishipper.net;

import android.util.Log;

import com.framgia.ishipper.server.RegisterResponse;
import com.framgia.ishipper.server.ShipperNearbyResponse;
import com.google.gson.Gson;

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

    public static Gson sConverter = new Gson();

    private static OkHttpClient loggingClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return httpClient.build();
    }

    private static final Retrofit builder = new Retrofit.Builder()
            .baseUrl(APIDefinition.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(loggingClient())
            .build();

    private static final APIServices client = builder.create(APIServices.class);

    // **** Common callback interface ***/
    public interface APICallback<T> {
        void onResponse(T response);

        void onFailure(int code, String message);
    }

    // ** API ******/


    /* Register User */
    public static void register(Map<String, String> userParams,
                                final APICallback<APIResponse<RegisterResponse>> callback) {
        client.signupUser(userParams).enqueue(new Callback<APIResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<RegisterResponse>> call,
                                   Response<APIResponse<RegisterResponse>> response) {
                if (response.body() != null) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<RegisterResponse>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void changePassword(
            String token,
            Map<String, String> params,
            final APICallback<APIResponse<APIResponse.ChangePasswordResponse>> callback) {
        client.changePassword(params, token).enqueue(new Callback<APIResponse<APIResponse.ChangePasswordResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<APIResponse.ChangePasswordResponse>> call,
                                   Response<APIResponse<APIResponse.ChangePasswordResponse>> response) {
                Log.d(TAG, "onResponse: ");
                if (response.body() != null && response.code() == 1) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<APIResponse.ChangePasswordResponse>> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void signIn(Map<String, String> params,
                              final APICallback<APIResponse<APIResponse.SignInResponse>> callback) {
        client.signIn(params).enqueue(new Callback<APIResponse<APIResponse.SignInResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<APIResponse.SignInResponse>> call,
                                   Response<APIResponse<APIResponse.SignInResponse>> response) {
                if (response.body() != null && response.body().getCode() == 1) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<APIResponse.SignInResponse>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });

    }

    public static void getConfirmationPin(String phoneNumber,
                                          final APICallback<APIResponse<APIResponse.EmptyResponse>> callback) {
        client.getConfirmationPin(phoneNumber).enqueue(new Callback<APIResponse<APIResponse.EmptyResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<APIResponse.EmptyResponse>> call,
                                   Response<APIResponse<APIResponse.EmptyResponse>> response) {
                if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<APIResponse.EmptyResponse>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void getCheckPin(String phoneNumber, String pin,
                                   final APICallback<APIResponse<APIResponse.EmptyResponse>> callback) {
        client.getCheckPin(phoneNumber, pin).enqueue(new Callback<APIResponse<APIResponse.EmptyResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<APIResponse.EmptyResponse>> call,
                                   Response<APIResponse<APIResponse.EmptyResponse>> response) {
                if (response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<APIResponse.EmptyResponse>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void putResetPassword(HashMap<String, String> params,
                                        final APICallback<APIResponse<APIResponse.EmptyResponse>> callback) {
        client.resetPassword(params).enqueue(
                new Callback<APIResponse<APIResponse.EmptyResponse>>() {
                    @Override
                    public void onResponse(Call<APIResponse<APIResponse.EmptyResponse>> call,
                                           Response<APIResponse<APIResponse.EmptyResponse>> response) {
                        if (response.body().isSuccess()) {
                            callback.onResponse(response.body());
                        } else {
                            callback.onFailure(response.body().getCode(),
                                               response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<APIResponse.EmptyResponse>> call,
                                          Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
    }
    /* Get shipper nearby */
    public static void getShipperNearby(String token,
                                        Map<String, String> userParams,
                                        final APICallback<APIResponse<ShipperNearbyResponse>> callback) {
        client.getShipperNearby(token, userParams).enqueue(
                new Callback<APIResponse<ShipperNearbyResponse>>() {
                    @Override
                    public void onResponse(Call<APIResponse<ShipperNearbyResponse>> call,
                                           Response<APIResponse<ShipperNearbyResponse>> response) {

                        if (response.body().isSuccess()) {
                            callback.onResponse(response.body());
                        } else {
                            callback.onFailure(response.body().getCode(),
                                               response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<ShipperNearbyResponse>> call,
                                          Throwable t) {
                        callback.onFailure(LOCAL_ERROR, t.getMessage());
                    }
                });
    }
}
