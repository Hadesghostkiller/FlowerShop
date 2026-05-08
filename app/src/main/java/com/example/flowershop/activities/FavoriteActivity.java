package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.FavoriteItem;
import com.example.flowershop.model.SupabaseFlower;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private FlowerAdapter adapter;
    private TextView tvEmpty;
    private FirebaseAuth mAuth;
    private View navHomeContainer, navWishlistContainer, navCartContainer, navProfileContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mAuth = FirebaseAuth.getInstance();
        initViews();
        setupRecyclerView();
        setupBottomNavigation();
        loadFavorites();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // Hiệu ứng nhẹ khi vào trang
        if (navWishlistContainer != null) {
            animateSelection(navWishlistContainer);
        }
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmpty = findViewById(R.id.tvEmpty);
        navHomeContainer = findViewById(R.id.navHomeContainer);
        navWishlistContainer = findViewById(R.id.navWishlistContainer);
        navCartContainer = findViewById(R.id.navCartContainer);
        navProfileContainer = findViewById(R.id.navProfileContainer);
    }

    private void setupRecyclerView() {
        adapter = new FlowerAdapter(flower -> addToCart(flower), R.layout.item_flower_grid);
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavorites.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        navHomeContainer.setOnClickListener(v -> {
            animateSelection(v);
            Intent intent = new Intent(this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        navWishlistContainer.setOnClickListener(v -> {
            animateSelection(v);
        });

        navCartContainer.setOnClickListener(v -> {
            animateSelection(v);
            startActivity(new Intent(this, CartActivity.class));
        });

        navProfileContainer.setOnClickListener(v -> {
            animateSelection(v);
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void animateSelection(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.8f, 1.0f,
                0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(300);
        view.startAnimation(scaleAnimation);
    }

    private void loadFavorites() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        SupabaseClient.getApi().getFavoritesByUserId("eq." + user.getUid()).enqueue(new Callback<List<FavoriteItem>>() {
            @Override
            public void onResponse(Call<List<FavoriteItem>> call, Response<List<FavoriteItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SupabaseFlower> flowers = new ArrayList<>();
                    for (FavoriteItem item : response.body()) {
                        if (item.getFlowers() != null) {
                            flowers.add(item.getFlowers());
                        }
                    }
                    adapter.setFlowersFromSupabase(flowers);
                    tvEmpty.setVisibility(flowers.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteItem>> call, Throwable t) {
                Log.e("FAVORITE_ERROR", t.getMessage());
            }
        });
    }

    private void addToCart(SupabaseFlower flower) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("user_id", user.getUid());
        cartData.put("flower_id", flower.id);
        cartData.put("quantity", 1);

        SupabaseClient.getApi().addToCart(cartData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FavoriteActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
