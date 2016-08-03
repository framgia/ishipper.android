package com.framgia.ishipper.server;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.ui.activity.RegisterActivity;
import com.framgia.ishipper.util.Const;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vuduychuong1994 on 8/4/16.
 */
public class ServerRequestUtils {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Const.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(loggingClient())
            .build();

    public static void register(User user, final CallBack callBack) {
        APIService apiService = retrofit.create(APIService.class);
        Call<UserResponse> requestCall = apiService.signupUser(user.getPhoneNumber(),
                                                         user.getPassword(),
                                                         user.getRole(),
                                                         user.getName());
        requestCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                callBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callBack.onFailure(t);
            }
        });
    }

    private static OkHttpClient loggingClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        return httpClient.build();
    }
}
