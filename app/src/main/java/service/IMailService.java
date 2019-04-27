package service;

import java.util.List;

import model.Folder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
}
