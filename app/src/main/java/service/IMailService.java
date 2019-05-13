package service;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import model.Account;
import model.Contact;
import model.Folder;
import model.Message;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
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

        @POST("/contacts/update")
        Call<Contact> updateContact(@Body() Contact contact);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MailService.BASE_URL + "/contacts/delete", hasBody = true)
//        @DELETE("/contacts/delete")
        Call<ArrayList<Contact>> deleteContact(@Field("contactId") int id);

        @FormUrlEncoded
        @POST("/update_profile")
        Call<Account> updateProfile(@Field("id") int id,@Field("username") String username, @Field("password") String password,@Field("protocol") String protocol);

        @GET("/messages")
        Call<ArrayList<Message>> getAllMessages();

        @POST("/contacts")
        Call<Contact> createContact(@Body() Contact contact);

        @POST("/folders")
        Call<Folder> createFolder(@Body() Folder folder);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MailService.BASE_URL + "/folders/delete", hasBody = true)
        Call<ArrayList<Folder>> deleteFolder(@Field("folderId") int id);



}
