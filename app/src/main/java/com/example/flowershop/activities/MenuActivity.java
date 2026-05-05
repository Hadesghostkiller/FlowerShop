package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.flowershop.R;
import com.example.flowershop.adapters.BannerAdapter;
import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.Banner;
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.sync.SupabaseSync;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {
    private ViewPager2 viewPagerBanner;
    private TabLayout tabDots;
    private RecyclerView rvBestSeller;
    private FlowerAdapter bestSellerAdapter;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView autoCompleteSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setupBottomNavigation();
        loadBanners();
        loadBestSellers();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabDots = findViewById(R.id.tabDots);
        rvBestSeller = findViewById(R.id.rvBestSeller);
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);

        autoCompleteSearch.setFocusable(false);
        autoCompleteSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));

        findViewById(R.id.btnMenuToggle).setOnClickListener(v -> Toast.makeText(this, "Menu...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnNotification).setOnClickListener(v -> Toast.makeText(this, "No notifications", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnChatbot).setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navCartContainer).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.navProfileContainer).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.navProfileContainer).setOnLongClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        });
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
                Log.e("BANNER_ERROR", t.getMessage());
            }
        });
    }

    private void loadBestSellers() {
        bestSellerAdapter = new FlowerAdapter(f -> Toast.makeText(this, f.flowerName, Toast.LENGTH_SHORT).show());
        rvBestSeller.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvBestSeller.setAdapter(bestSellerAdapter);

        SupabaseClient.getApi().getBestSellers().enqueue(new retrofit2.Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(retrofit2.Call<List<SupabaseFlower>> call, retrofit2.Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> bestSellerAdapter.setFlowersFromSupabase(response.body()));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<SupabaseFlower>> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(MenuActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
