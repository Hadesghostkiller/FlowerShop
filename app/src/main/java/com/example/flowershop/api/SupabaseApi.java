package com.example.flowershop.api;

import com.example.flowershop.model.SupabaseFlower;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SupabaseApi {
    @GET("rest/v1/flowers")
    Call<List<SupabaseFlower>> getFlowers(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authorization
    );

    // Helper method without headers (headers added via interceptor)
    @GET("rest/v1/flowers")
    Call<List<SupabaseFlower>> getFlowers();
}
