package service;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import model.Account;
import model.Contact;
import model.Folder;
import model.Message;
import retrofit2.Call;
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

        @GET("/contacts")
        Call<ArrayList<Contact>> getAllContacts();

        @GET("/messages")
        Call<ArrayList<Message>> getAllMessages();

}
