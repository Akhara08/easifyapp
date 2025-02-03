package com.example.soundguard;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    @GET("fetch_services.php")
    Call<List<ServiceItem>> getServices();
}
