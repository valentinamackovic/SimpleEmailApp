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
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface IMailService {

        @Headers({
                "User-Agent: Mobile-Android",
                "Content-Type:application/json"
        })

        //+++++++++ FOLDERS ++++++++++++++
        @GET("/folders")
        Call<List<Folder>> getAllFolders(@Query("userId") int userId);

        @POST("/folders")
        Call<Folder> createFolder(@Body() Folder folder, @Query("userId") int userId);

        @FormUrlEncoded
        @PUT("/folders")
        Call<ArrayList<Folder>> updateFolder(@Field("id") int id, @Field("folderName") String name, @Field("folderOperation") String folderOperation, @Field("folderCondition") String folderCondition);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MailService.BASE_URL + "/folders/delete", hasBody = true)
        Call<ArrayList<Folder>> deleteFolder(@Field("folderId") int id, @Field("userId") int userId);

        //+++++++++ CONTACTS +++++++++++++++++++++



        @GET("/contacts")
        Call<ArrayList<Contact>> getAllContacts(@Query("userId") int userId);


//        @PUT("/contacts/update")
        @PUT("/contacts")
        Call<Contact> updateContact(@Body() Contact contact, @Query("userId") int userId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MailService.BASE_URL + "/contacts/delete", hasBody = true)
//        @DELETE("/contacts/delete")
        Call<ArrayList<Contact>> deleteContact(@Field("contactId") int id, @Field("userId") int userId);

        @POST("/contacts")
        Call<Contact> createContact(@Body() Contact contact, @Query("userId") int userId);

        //++++++++ MESSAGES ++++++++++++++++
        @GET("/messages")
        Call<ArrayList<Message>> getAllMessages(@Query("userId") int id);

        @FormUrlEncoded
        @POST("/readMessage")
        Call<ArrayList<Message>> readMessage(@Field("userId") int userId, @Field("messageId") int messageId);

        @POST("/savetodraft")
        Call<Message> saveToDraft(@Body() Message message, @Query("userId") int userId);

        //+++++++++ OTHER +++++++++++++
        @FormUrlEncoded
        @POST("/login")
        Call<Account> login(@Field("username") String username, @Field("password") String password);

        @FormUrlEncoded
        @POST("/update_profile")
        Call<Account> updateProfile(@Field("id") int id,@Field("username") String username, @Field("password") String password,@Field("protocol") String protocol);




}
