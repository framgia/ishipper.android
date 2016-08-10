package com.framgia.ishipper.net;

import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.server.SignUpResponse;

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
                              final APICallback<APIResponse<SignUpResponse>> callback) {
        client.signUpUser(userParams).enqueue(new Callback<APIResponse<SignUpResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<SignUpResponse>> call,
                                   Response<APIResponse<SignUpResponse>> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<SignUpResponse>> call, Throwable t) {
                callback.onFailure(LOCAL_ERROR, t.getMessage());
            }
        });
    }

    public static void confirmationPinInSignup(String phoneNumber, String pin,
                                               final APICallback<APIResponse<APIResponse.EmptyResponse>> callback) {
        client.confirmationPinInSignup(phoneNumber, pin)
                .enqueue(new Callback<APIResponse<APIResponse.EmptyResponse>>() {
                    @Override
                    public void onResponse(Call<APIResponse<APIResponse.EmptyResponse>> call,
                                           Response<APIResponse<APIResponse.EmptyResponse>> response) {
                        if (response.isSuccessful() && response.body().isSuccess()) {
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
    public static void changePassword(
            String token,
            Map<String, String> params,
            final APICallback<APIResponse<APIResponse.ChangePasswordResponse>> callback) {
        client.changePassword(params, token).enqueue(new Callback<APIResponse<APIResponse.ChangePasswordResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<APIResponse.ChangePasswordResponse>> call,
                                   Response<APIResponse<APIResponse.ChangePasswordResponse>> response) {
                Log.d(TAG, "On Response");
                if (response.isSuccessful() && response.body().isSuccess()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(LOCAL_ERROR, response.body().getMessage());
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
                if (response.isSuccessful() && response.body().isSuccess()) {
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
                if (response.isSuccessful() && response.body().isSuccess()) {
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
                if (response.isSuccessful() && response.body().isSuccess()) {
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

    public static void postResetPassword(HashMap<String, String> params,
                                         final APICallback<APIResponse<APIResponse.EmptyResponse>> callback) {
        client.resetPassword(params).enqueue(new Callback<APIResponse<APIResponse.EmptyResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<APIResponse.EmptyResponse>> call,
                                   Response<APIResponse<APIResponse.EmptyResponse>> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
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

}
