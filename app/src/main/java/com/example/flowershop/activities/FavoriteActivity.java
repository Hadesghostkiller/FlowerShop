package com.example.flowershop.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    // Bottom Navigation Views
    private ViewGroup bottomNavContainer;
    private LinearLayout navHome, navWishlist, navCart, navProfile;
    private TextView tvHome, tvWishlist, tvCart, tvProfile;
    private ImageView ivHome, ivWishlist, ivCart, ivProfile;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo tab Wishlist được Active khi ở trang này
        updateTabUI(R.id.navWishlistContainer);
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Ánh xạ Container điều hướng
        bottomNavContainer = findViewById(R.id.bottomNavContainer);
        navHome = findViewById(R.id.navHomeContainer);
        navWishlist = findViewById(R.id.navWishlistContainer);
        navCart = findViewById(R.id.navCartContainer);
        navProfile = findViewById(R.id.navProfileContainer);

        // Ánh xạ Icons và Texts để đổi màu/ẩn hiện
        ivHome = findViewById(R.id.navHomeIcon);
        ivWishlist = findViewById(R.id.navWishlistIcon);
        ivCart = findViewById(R.id.navCartIcon);
        ivProfile = findViewById(R.id.navProfileIcon);

        tvHome = findViewById(R.id.navHomeText);
        tvWishlist = findViewById(R.id.navWishlistText);
        tvCart = findViewById(R.id.navCartText);
        tvProfile = findViewById(R.id.navProfileText);
    }

    private void setupRecyclerView() {
        adapter = new FlowerAdapter(flower -> addToCart(flower), R.layout.item_flower_grid);
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavorites.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        // Chuyển về Home (Menu)
        navHome.setOnClickListener(v -> {
            updateTabUI(R.id.navHomeContainer);
            v.postDelayed(() -> {
                Intent intent = new Intent(this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 150);
        });

        // Tab hiện tại (Wishlist)
        navWishlist.setOnClickListener(v -> {
            updateTabUI(R.id.navWishlistContainer);
        });

        // Chuyển sang Giỏ hàng
        navCart.setOnClickListener(v -> {
            updateTabUI(R.id.navCartContainer);
            v.postDelayed(() -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 150);
        });

        // Chuyển sang Hồ sơ
        navProfile.setOnClickListener(v -> {
            updateTabUI(R.id.navProfileContainer);
            v.postDelayed(() -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 150);
        });
    }

    /**
     * Logic trung tâm sử dụng TransitionManager để tạo hiệu ứng "viên thuốc" mượt mà
     */
    private void updateTabUI(int clickedId) {
        if (bottomNavContainer == null) return;

        // Tạo hiệu ứng chuyển cảnh tự động cho Layout
        AutoTransition transition = new AutoTransition();
        transition.setDuration(250);
        TransitionManager.beginDelayedTransition(bottomNavContainer, transition);

        int[] containerIds = {R.id.navHomeContainer, R.id.navWishlistContainer, R.id.navCartContainer, R.id.navProfileContainer};
        LinearLayout[] containers = {navHome, navWishlist, navCart, navProfile};
        TextView[] texts = {tvHome, tvWishlist, tvCart, tvProfile};
        ImageView[] icons = {ivHome, ivWishlist, ivCart, ivProfile};

        for (int i = 0; i < containerIds.length; i++) {
            if (containers[i] == null) continue;

            if (containerIds[i] == clickedId) {
                // Tab được Active: Hiện nền pill, hiện chữ, đổi màu hồng
                containers[i].setBackgroundResource(R.drawable.bg_pill_active);
                if (texts[i] != null) texts[i].setVisibility(View.VISIBLE);
                if (icons[i] != null) icons[i].setColorFilter(Color.parseColor("#E91E63"));
            } else {
                // Tab Inactive: Nền trong suốt, ẩn chữ, màu xám
                containers[i].setBackgroundColor(Color.TRANSPARENT);
                if (texts[i] != null) texts[i].setVisibility(View.GONE);
                if (icons[i] != null) icons[i].setColorFilter(Color.parseColor("#FFFFFF"));
            }
        }
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
                Log.e("FAVORITE_ERROR", "Lỗi tải yêu thích: " + t.getMessage());
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
                Toast.makeText(FavoriteActivity.this, "Lỗi thêm giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}