package com.example.flowershop.api;

import com.example.flowershop.model.Banner;
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.model.CartItem; // Import Model CartItem bạn vừa tạo

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    @GET("rest/v1/flowers?select=*")
    Call<List<SupabaseFlower>> getFlowers();

    @GET("rest/v1/flowers?select=*&order=luot_mua.desc&limit=10")
    Call<List<SupabaseFlower>> getBestSellers();

    @GET("rest/v1/banners?select=*")
    Call<List<Banner>> getBanners();

    @GET("rest/v1/cart?select=*,flowers(*)")
    Call<List<CartItem>> getCartByUserId(@Query(value = "user_id", encoded = true) String userIdEq);

    // THÊM MỚI: Hàm POST để lưu sản phẩm vào bảng cart
    @POST("rest/v1/cart")
    Call<Void> addToCart(@Body Map<String, Object> cartData);
}