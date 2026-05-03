package com.example.flowershop.api;

import com.example.flowershop.model.SupabaseFlower;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SupabaseApi {
    @GET("rest/v1/flowers")
    Call<List<SupabaseFlower>> getFlowers();
}