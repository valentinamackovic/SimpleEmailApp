package service;

import java.util.List;

import model.Account;
import model.Folder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IMailService {

        @Headers({
                "User-Agent: Mobile-Android",
                "Content-Type:application/json"
        })
        @GET("/folders")
        Call<List<Folder>> getAllFolders();

        @FormUrlEncoded
        @POST("/login")
        Call<Account> login(@Field("username") String username, @Field("password") String password);
}
