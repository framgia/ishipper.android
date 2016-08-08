package com.framgia.ishipper.net;

import com.framgia.ishipper.server.RegisterResponse;
import com.google.gson.Gson;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HungNT on 8/5/16.
 */
public abstract class API {
    public static Gson sConverter = new Gson();

    private static final Retrofit builder =
            new Retrofit.Builder().baseUrl(APIDefinition.getBaseUrl()).addConverterFactory(
                    GsonConverterFactory.create()).build();

    private static final APIServices client = builder.create(APIServices.class);

    // **** Common callback interface ***/
    public interface APICallback<T> {
        void onResponse(T response);

        void onFailure(int code, String message);
    }

    // ** API ******/
    public static void updateProfile(String userId, Map<String, String> params,
                                     final APICallback<APIResponse.PutUpdateProfileResponse> callback) {
        client.updateProfile(userId, params).enqueue(
                new Callback<APIResponse.PutUpdateProfileResponse>() {
                    @Override
                    public void onResponse(Call<APIResponse.PutUpdateProfileResponse> call,
                                           Response<APIResponse.PutUpdateProfileResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().success) {
                                callback.onResponse(response.body());
                            } else {
                                callback.onFailure(response.body().getCode(),
                                                   response.body().getMessage());
                            }
                        } else {
                            callback.onFailure(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse.PutUpdateProfileResponse> call,
                                          Throwable t) {
                        callback.onFailure(APIError.LOCAL_ERROR, t.getMessage());
                    }
                });
    }

    public static void register(Map<String, String> userParams,
                                final APICallback<APIResponse<RegisterResponse>> callback) {
        client.signupUser(userParams).enqueue(new Callback<APIResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<RegisterResponse>> call,
                                   Response<APIResponse<RegisterResponse>> response) {
                if (response.body() != null) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(APIError.LOCAL_ERROR, response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<RegisterResponse>> call, Throwable t) {
                callback.onFailure(APIError.LOCAL_ERROR, t.getMessage());
            }
        });
    }
}
