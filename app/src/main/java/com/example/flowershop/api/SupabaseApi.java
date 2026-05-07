package com.example.flowershop.api;

import com.example.flowershop.model.Banner;
import com.example.flowershop.model.Category;
import com.example.flowershop.model.FavoriteItem;
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.model.CartItem;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    @GET("rest/v1/flowers?select=*")
    Call<List<SupabaseFlower>> getFlowers();

    @GET("rest/v1/flowers?select=*&order=luot_mua.desc&limit=10")
    Call<List<SupabaseFlower>> getBestSellers();

    @GET("rest/v1/banners?select=*")
    Call<List<Banner>> getBanners();

    @GET("rest/v1/category?select=*")
    Call<List<Category>> getCategories();

    @GET("rest/v1/flowers?select=*")
    Call<List<SupabaseFlower>> getFlowersByCategory(@Query("category_id") String categoryIdEq);

    @GET("rest/v1/cart?select=*,flowers(*)")
    Call<List<CartItem>> getCartByUserId(@Query(value = "user_id", encoded = true) String userIdEq);

    @POST("rest/v1/cart")
    Call<Void> addToCart(@Body Map<String, Object> cartData);

    @PATCH("rest/v1/cart")
    Call<Void> updateCartQuantity(
            @Query(value = "user_id", encoded = true) String userIdEq,
            @Query(value = "flower_id", encoded = true) String flowerIdEq,
            @Body Map<String, Object> updates
    );

    @DELETE("rest/v1/cart")
    Call<Void> deleteCartItem(
            @Query(value = "user_id", encoded = true) String userIdEq,
            @Query(value = "flower_id", encoded = true) String flowerIdEq
    );

    // Favorite APIs
    @GET("rest/v1/favorite?select=*,flowers(*)")
    Call<List<FavoriteItem>> getFavoritesByUserId(@Query(value = "user_id", encoded = true) String userIdEq);

    @POST("rest/v1/favorite")
    Call<Void> addToFavorite(@Body Map<String, Object> favoriteData);

    @DELETE("rest/v1/favorite")
    Call<Void> deleteFavoriteItem(
            @Query(value = "user_id", encoded = true) String userIdEq,
            @Query(value = "flower_id", encoded = true) String flowerIdEq
    );

    // Flower Management APIs
    @POST("rest/v1/flowers")
    Call<Void> addFlower(@Body Map<String, Object> flowerData);

    @PATCH("rest/v1/flowers")
    Call<Void> updateFlower(
            @Query(value = "id", encoded = true) String idEq,
            @Body Map<String, Object> flowerData
    );

    @DELETE("rest/v1/flowers")
    Call<Void> deleteFlower(@Query(value = "id", encoded = true) String idEq);
}
