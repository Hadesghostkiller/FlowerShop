package com.example.flowershop.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {
    private static final String BASE_URL = "https://iwqklcrgfjngmvpzxmsy.supabase.co/";
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml3cWtsY3JnZmpuZ212cHp4bXN5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc4MDcyMjIsImV4cCI6MjA5MzM4MzIyMn0.-gcVvphhyZ1NUH_EszFxfWxMJ9lxDaBx7kuA9yVIIUM";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder requestBuilder = original.newBuilder()
                                .header("apikey", ANON_KEY)
                                .header("Authorization", "Bearer " + ANON_KEY)
                                .method(original.method(), original.body());
                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static SupabaseApi getApi() {
        return getClient().create(SupabaseApi.class);
    }
}