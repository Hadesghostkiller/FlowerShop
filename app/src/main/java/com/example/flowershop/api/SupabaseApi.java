package com.example.flowershop.api;

import com.example.flowershop.model.Banner;
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.model.CartItem;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @POST("rest/v1/cart")
    Call<Void> addToCart(@Body Map<String, Object> cartData);

    // THÊM MỚI: Hàm xóa một loại hoa khỏi giỏ hàng của User
    @DELETE("rest/v1/cart")
    Call<Void> deleteCartItem(
            @Query(value = "user_id", encoded = true) String userIdEq,
            @Query(value = "flower_id", encoded = true) String flowerIdEq
    );
}