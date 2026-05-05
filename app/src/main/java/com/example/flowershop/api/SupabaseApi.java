package com.example.flowershop.api;

import com.example.flowershop.model.Banner;
import com.example.flowershop.model.SupabaseFlower;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SupabaseApi {
    @GET("rest/v1/flowers?select=*")
    Call<List<SupabaseFlower>> getFlowers();

    @GET("rest/v1/flowers?select=*&order=luot_mua.desc&limit=10")
    Call<List<SupabaseFlower>> getBestSellers();

    @GET("rest/v1/banners?select=*")
    Call<List<Banner>> getBanners();
}