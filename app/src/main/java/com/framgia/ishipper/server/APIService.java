package com.framgia.ishipper.server;
import com.framgia.ishipper.model.User;
import com.framgia.ishipper.util.Const;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by vuduychuong1994 on 8/3/16.
 */
public interface APIService {


    @FormUrlEncoded
    @POST(Const.SIGNUP_PATH)
    Call<UserResponse> signupUser(@Field(Const.USER_PHONE_NUMBER) String phoneNum,
                                  @Field(Const.USER_PASSWORD) String password,
                                  @Field(Const.ROLE) int role,
                                  @Field(Const.NAME) String name);
}
