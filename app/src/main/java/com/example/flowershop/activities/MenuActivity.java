package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.flowershop.R;
import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.sync.SupabaseSync;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private ViewPager2 viewPagerBanner;
    private RecyclerView rvCategory, rvOccasions, rvBestSeller;
    private AutoCompleteTextView searchBar;
    private FlowerAdapter bestSellerAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupBottomNavigation();
        setupBanner();
        loadBestSellers();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        rvCategory = findViewById(R.id.rvCategory);
        rvOccasions = findViewById(R.id.rvOccasions);
        rvBestSeller = findViewById(R.id.rvBestSeller);
        searchBar = findViewById(R.id.autoCompleteSearch);

        // Nút Menu/Drawer (Tạm thời hiện Toast)
        findViewById(R.id.btnMenuToggle).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng Menu đang phát triển", Toast.LENGTH_SHORT).show());

        // Nút Thông báo
        findViewById(R.id.btnNotification).setOnClickListener(v ->
                Toast.makeText(this, "Bạn không có thông báo mới", Toast.LENGTH_SHORT).show());

        // Nút AI Chatbot
        findViewById(R.id.btnChatbot).setOnClickListener(v ->
                startActivity(new Intent(this, ChatbotActivity.class)));
    }

    private void setupBottomNavigation() {
        // Ánh xạ các Container (Vùng nhấn) của thanh điều hướng dưới
        LinearLayout navHome = findViewById(R.id.navHomeContainer);
        LinearLayout navCart = findViewById(R.id.navCartContainer);
        LinearLayout navProfile = findViewById(R.id.navProfileContainer);
        LinearLayout navWishlist = findViewById(R.id.navWishlistContainer);

        navHome.setOnClickListener(v -> {
            // Đang ở Home nên không cần chuyển trang
        });

        navCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        navWishlist.setOnClickListener(v -> {
            Toast.makeText(this, "Mục yêu thích sẽ sớm ra mắt!", Toast.LENGTH_SHORT).show();
        });

        // Logout logic (Ví dụ: Nhấn giữ icon Profile để đăng xuất)
        navProfile.setOnLongClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        });
    }

    private void setupBanner() {
        // Ở đây bạn sẽ gắn Adapter cho ViewPager2 để chạy banner ảnh
        // Hiện tại để trống để tránh lỗi compile nếu bạn chưa có BannerAdapter
    }

    private void loadBestSellers() {
        bestSellerAdapter = new FlowerAdapter(flower -> {
            // Logic khi nhấn mua hoa ở mục Best Seller
            Toast.makeText(this, "Đã chọn: " + flower.flowerName, Toast.LENGTH_SHORT).show();
        });

        rvBestSeller.setLayoutManager(new GridLayoutManager(this, 2));
        rvBestSeller.setAdapter(bestSellerAdapter);

        // Gọi dữ liệu từ Supabase
        SupabaseSync.getFlowers(new SupabaseSync.FlowerCallback() {
            @Override
            public void onSuccess(List<SupabaseFlower> flowers) {
                runOnUiThread(() -> bestSellerAdapter.setFlowersFromSupabase(flowers));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MenuActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
            }
        });
    }
}