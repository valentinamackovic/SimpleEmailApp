package service;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MailService {

    private static Retrofit retrofit;
    //private static final String BASE_URL = "http://192.168.1.7:8080";
    public static final String BASE_URL = "http://192.168.0.12:8080";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
