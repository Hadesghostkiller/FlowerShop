package com.example.flowershop.sync;

import android.util.Log;

import com.example.flowershop.api.SupabaseApi;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.SupabaseFlower;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupabaseSync {

    private static final String TAG = "SupabaseSync";

    public interface FlowerCallback {
        void onSuccess(List<SupabaseFlower> flowers);
        void onError(String error);
    }

    public static void getFlowers(FlowerCallback callback) {
        SupabaseApi api = SupabaseClient.getApi();
        Call<List<SupabaseFlower>> call = api.getFlowers();
        call.enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Got " + response.body().size() + " flowers from Supabase");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Failed: " + response.code());
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }
}