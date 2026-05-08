package com.example.flowershop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.SupabaseFlower;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private FlowerAdapter flowerAdapter;
    private TextView tvCategoryTitle;
    private ProgressBar progressBar;
    private int categoryId;
    private String categoryName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mAuth = FirebaseAuth.getInstance();
        categoryId = getIntent().getIntExtra("category_id", -1);
        categoryName = getIntent().getStringExtra("category_name");

        initViews();
        loadProducts();
    }

    private void initViews() {
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);

        tvCategoryTitle.setText(categoryName != null ? categoryName : "Danh mục");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Thiết kế dạng lưới 2 cột
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Sử dụng item_flower_grid để hiển thị đẹp hơn trong GridView
        flowerAdapter = new FlowerAdapter(this::addToCartToSupabase, R.layout.item_flower_grid);
        rvProducts.setAdapter(flowerAdapter);
    }

    private void loadProducts() {
        if (categoryId == -1) return;

        progressBar.setVisibility(View.VISIBLE);
        // Supabase filter: category_id=eq.X
        String filter = "eq." + categoryId;
        
        SupabaseClient.getApi().getFlowersByCategory(filter).enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    flowerAdapter.setFlowersFromSupabase(response.body());
                } else {
                    Toast.makeText(CategoryActivity.this, "Không có sản phẩm nào cho danh mục này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("CATEGORY_LOAD_ERROR", t.getMessage());
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCartToSupabase(SupabaseFlower flower) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CategoryActivity.this, "Đã thêm vào giỏ!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
