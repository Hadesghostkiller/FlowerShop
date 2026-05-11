package com.example.flowershop.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.flowershop.R;
import com.example.flowershop.adapters.BannerAdapter;
import com.example.flowershop.adapters.CategoryAdapter;
import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.Banner;
import com.example.flowershop.model.Category;
import com.example.flowershop.model.FavoriteItem;
import com.example.flowershop.model.SupabaseFlower;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    // Views dữ liệu
    private ViewPager2 viewPagerBanner;
    private TabLayout tabDots;
    private RecyclerView rvCategory, rvBestSeller;
    private CategoryAdapter categoryAdapter;
    private FlowerAdapter bestSellerAdapter;
    private AutoCompleteTextView autoCompleteSearch;

    // Views Bottom Navigation (Google Nav Bar Style)
    private ViewGroup bottomNavContainer;
    private LinearLayout navHome, navWishlist, navCart, navProfile;
    private TextView tvHome, tvWishlist, tvCart, tvProfile;
    private ImageView ivHome, ivWishlist, ivCart, ivProfile;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupBottomNavigation();
        loadBanners();
        loadCategories();
        loadBestSellers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        // Đảm bảo khi quay lại Menu thì tab Home được chọn
        updateTabUI(R.id.navHomeContainer);
    }

    private void initViews() {
        // Ánh xạ các view chính
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabDots = findViewById(R.id.tabDots);
        rvCategory = findViewById(R.id.rvCategory);
        rvBestSeller = findViewById(R.id.rvBestSeller);
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);

        // Ánh xạ Bottom Nav
        bottomNavContainer = findViewById(R.id.bottomNavContainer);
        navHome = findViewById(R.id.navHomeContainer);
        navWishlist = findViewById(R.id.navWishlistContainer);
        navCart = findViewById(R.id.navCartContainer);
        navProfile = findViewById(R.id.navProfileContainer);

        ivHome = findViewById(R.id.navHomeIcon);
        ivWishlist = findViewById(R.id.navWishlistIcon);
        ivCart = findViewById(R.id.navCartIcon);
        ivProfile = findViewById(R.id.navProfileIcon);

        tvHome = findViewById(R.id.navHomeText);
        tvWishlist = findViewById(R.id.navWishlistText);
        tvCart = findViewById(R.id.navCartText);
        tvProfile = findViewById(R.id.navProfileText);

        // Thiết lập Search
        autoCompleteSearch.setFocusable(false);
        autoCompleteSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));

        // Các nút Toolbar
        findViewById(R.id.btnNotification).setOnClickListener(v -> Toast.makeText(this, "Không có thông báo mới", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnChatbot).setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));
    }

    private void setupBottomNavigation() {
        // Tab Home
        navHome.setOnClickListener(v -> updateTabUI(R.id.navHomeContainer));

        // Tab Yêu thích
        navWishlist.setOnClickListener(v -> {
            updateTabUI(R.id.navWishlistContainer);
            v.postDelayed(() -> {
                startActivity(new Intent(this, FavoriteActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 200);
        });

        // Tab Giỏ hàng
        navCart.setOnClickListener(v -> {
            updateTabUI(R.id.navCartContainer);
            v.postDelayed(() -> {
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 200);
        });

        // Tab Hồ sơ
        navProfile.setOnClickListener(v -> {
            updateTabUI(R.id.navProfileContainer);
            v.postDelayed(() -> {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 200);
        });

        // Đăng xuất khi nhấn giữ Profile
        navProfile.setOnLongClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        });
    }

    /**
     * Hiệu ứng Google Nav Bar: Nở rộng tab được chọn, thu hẹp các tab khác
     */
    private void updateTabUI(int clickedId) {
        // Sử dụng TransitionManager để tạo hiệu ứng mượt mà khi thay đổi visibility
        AutoTransition transition = new AutoTransition();
        transition.setDuration(250);
        TransitionManager.beginDelayedTransition(bottomNavContainer, transition);

        // Mảng các container để lặp
        int[] containerIds = {R.id.navHomeContainer, R.id.navWishlistContainer, R.id.navCartContainer, R.id.navProfileContainer};
        View[] containers = {navHome, navWishlist, navCart, navProfile};
        TextView[] texts = {tvHome, tvWishlist, tvCart, tvProfile};
        ImageView[] icons = {ivHome, ivWishlist, ivCart, ivProfile};

        for (int i = 0; i < containerIds.length; i++) {
            if (containerIds[i] == clickedId) {
                // Tab được kích hoạt
                containers[i].setBackgroundResource(R.drawable.bg_pill_active);
                texts[i].setVisibility(View.VISIBLE);
                icons[i].setColorFilter(Color.parseColor("#E91E63"));
            } else {
                // Các tab còn lại
                containers[i].setBackgroundColor(Color.TRANSPARENT);
                texts[i].setVisibility(View.GONE);
                icons[i].setColorFilter(Color.parseColor("#FFFFFF"));
            }
        }
    }

    private void loadBanners() {
        SupabaseClient.getApi().getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BannerAdapter adapter = new BannerAdapter(MenuActivity.this, response.body());
                    viewPagerBanner.setAdapter(adapter);
                    new TabLayoutMediator(tabDots, viewPagerBanner, (tab, position) -> {}).attach();
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.e("BANNER_ERROR", "Lỗi tải banner: " + t.getMessage());
            }
        });
    }

    private void loadCategories() {
        categoryAdapter = new CategoryAdapter(category -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra("category_id", category.id);
            intent.putExtra("category_name", category.name);
            startActivity(intent);
        });
        rvCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategory.setAdapter(categoryAdapter);

        SupabaseClient.getApi().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setCategories(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("CATEGORY_ERROR", "Lỗi tải danh mục: " + t.getMessage());
            }
        });
    }

    private void loadBestSellers() {
        bestSellerAdapter = new FlowerAdapter(flower -> addToCartToSupabase(flower));
        rvBestSeller.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvBestSeller.setAdapter(bestSellerAdapter);

        SupabaseClient.getApi().getBestSellers().enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        bestSellerAdapter.setFlowersFromSupabase(response.body());
                        loadFavorites();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                Log.e("FLOWER_ERROR", "Lỗi tải sản phẩm: " + t.getMessage());
            }
        });
    }

    private void loadFavorites() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        SupabaseClient.getApi().getFavoritesByUserId("eq." + user.getUid()).enqueue(new Callback<List<FavoriteItem>>() {
            @Override
            public void onResponse(Call<List<FavoriteItem>> call, Response<List<FavoriteItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> ids = new ArrayList<>();
                    for (FavoriteItem item : response.body()) {
                        ids.add((int) item.getFlower_id());
                    }
                    if (bestSellerAdapter != null) {
                        bestSellerAdapter.setFavoriteFlowerIds(ids);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteItem>> call, Throwable t) {
                Log.e("FAVORITE_ERROR", "Lỗi tải yêu thích: " + t.getMessage());
            }
        });
    }

    private void addToCartToSupabase(SupabaseFlower flower) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("user_id", user.getUid());
        cartData.put("flower_id", flower.id);
        cartData.put("quantity", 1);

        SupabaseClient.getApi().addToCart(cartData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MenuActivity.this, "Đã thêm " + flower.flowerName + " vào giỏ!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MenuActivity.this, "Lỗi thêm vào giỏ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}