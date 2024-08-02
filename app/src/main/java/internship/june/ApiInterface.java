package internship.june;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("signup.php")
    Call<GetSignupData> doSignupData(
            @Field("name") String name,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<GetLoginData> doLoginData(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("update_profile.php")
    Call<GetSignupData> doUpdateProfileData(
            @Field("userid") String userid,
            @Field("name") String name,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("delete_profile.php")
    Call<GetSignupData> doDeleteData(@Field("userid") String userid);

}