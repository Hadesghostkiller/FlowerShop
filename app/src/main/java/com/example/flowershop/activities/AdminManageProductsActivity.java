package com.example.flowershop.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.AdminProductAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.SupabaseFlower;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminManageProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ImageButton btnBack;

    private AdminProductAdapter adapter;
    private List<SupabaseFlower> flowerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_products);

        recyclerView = findViewById(R.id.rvAdminProducts);
        fabAdd = findViewById(R.id.fabAddProduct);
        btnBack = findViewById(R.id.btnBackManage);

        flowerList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminProductAdapter(this, flowerList, new AdminProductAdapter.OnAdminProductListener() {
            @Override
            public void onEditClick(SupabaseFlower flower) {
                // CHUYỂN SANG ACTIVITY ĐỂ SỬA
                Intent intent = new Intent(AdminManageProductsActivity.this, AdminAddEditProductActivity.class);
                intent.putExtra("FLOWER_ID", flower.id);
                intent.putExtra("FLOWER_NAME", flower.flowerName);
                intent.putExtra("FLOWER_PRICE", flower.price);
                intent.putExtra("FLOWER_IMAGE", flower.imageResource);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(SupabaseFlower flower) {
                deleteProduct(flower);
            }
        });
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        // CHUYỂN SANG ACTIVITY ĐỂ THÊM MỚI
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageProductsActivity.this, AdminAddEditProductActivity.class);
            startActivity(intent);
        });
    }

    // Tự động làm mới danh sách khi quay lại màn hình này từ trang Thêm/Sửa
    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        SupabaseClient.getApi().getFlowers().enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    flowerList.clear();
                    flowerList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                Toast.makeText(AdminManageProductsActivity.this, "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct(SupabaseFlower flower) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa " + flower.flowerName + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    SupabaseClient.getApi().deleteFlower("eq." + flower.id).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AdminManageProductsActivity.this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                                loadProducts();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}