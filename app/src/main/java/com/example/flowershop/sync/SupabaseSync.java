package com.example.flowershop.sync;

import android.content.Context;
import android.util.Log;

import com.example.flowershop.api.SupabaseApi;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.database.FlowerDatabase;
import com.example.flowershop.model.SupabaseFlower;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupabaseSync {

    private static final String TAG = "SupabaseSync";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void syncFlowers(Context context) {
        executor.execute(() -> {
            try {
                SupabaseApi api = SupabaseClient.getApi();
                Call<List<SupabaseFlower>> call = api.getFlowers();
                call.enqueue(new Callback<List<SupabaseFlower>>() {
                    @Override
                    public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                        try {
                            Log.d(TAG, "Response code: " + response.code());

                            if (response.isSuccessful() && response.body() != null) {
                                List<SupabaseFlower> supabaseFlowers = response.body();
                                Log.d(TAG, "Got " + supabaseFlowers.size() + " flowers from Supabase");

                                FlowerDatabase db = FlowerDatabase.getDatabase(context);

                                java.util.ArrayList<com.example.flowershop.database.entity.Flower> localFlowers = 
                                        new java.util.ArrayList<>();
                                for (SupabaseFlower sf : supabaseFlowers) {
                                    localFlowers.add(sf.toFlower());
                                }

                                Log.d(TAG, "Converted to " + localFlowers.size() + " local flowers");

                                db.flowerDao().deleteAll();
                                Log.d(TAG, "Deleted old data");

                                db.flowerDao().insertFlowers(localFlowers);
                                Log.d(TAG, "Inserted new data");

                                Log.d(TAG, "Synced " + localFlowers.size() + " flowers from Supabase");

                                db.close();
                                exportDatabase(context);
                            } else {
                                Log.e(TAG, "Supabase sync failed: " + response.code());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error in onResponse: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                        Log.e(TAG, "Supabase sync error: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Sync exception: " + e.getMessage());
            }
        });
    }

    private static void exportDatabase(Context context) {
        try {
            FlowerDatabase db = FlowerDatabase.getDatabase(context);
            db.close();

            String dbPath = context.getDatabasePath("flowershop_db").getPath();
            java.io.File dbFile = new java.io.File(dbPath);

            java.io.File exportDir = new java.io.File(context.getExternalFilesDir(null), "database_export");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            java.io.File exportFile = new java.io.File(exportDir, "flowershop_db");

            java.io.FileInputStream fis = new java.io.FileInputStream(dbFile);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(exportFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            fis.close();

            Log.d(TAG, "Database exported to: " + exportFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Export database failed: " + e.getMessage());
        }
    }
}