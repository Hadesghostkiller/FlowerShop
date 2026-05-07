package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private ImageButton btnBackAdmin;
    private TextView tvAdminRevenue, tvAdminOrders;
    private Button btnUploadProduct, btnManageProducts, btnManageOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupListeners();

        // TODO: Sau này sẽ gọi API Supabase để lấy dữ liệu thực tế
        loadFakeData();
    }

    private void initViews() {
        btnBackAdmin = findViewById(R.id.btnBackAdmin);
        tvAdminRevenue = findViewById(R.id.tvAdminRevenue);
        tvAdminOrders = findViewById(R.id.tvAdminOrders);
        btnUploadProduct = findViewById(R.id.btnUploadProduct);
        btnManageProducts = findViewById(R.id.btnManageProducts);
        btnManageOrders = findViewById(R.id.btnManageOrders);
    }

    private void setupListeners() {
        btnBackAdmin.setOnClickListener(v -> finish());

        // ĐÃ SỬA CHỖ NÀY: Đi thẳng vào màn hình Thêm Hoa (AdminAddEditProductActivity)
        btnUploadProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAddEditProductActivity.class);
            startActivity(intent);
        });

        // Đi đến màn hình Quản lý Danh mục (AdminManageProductsActivity)
        btnManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminManageProductsActivity.class);
            startActivity(intent);
        });

        btnManageOrders.setOnClickListener(v -> {
            Toast.makeText(this, "Mở danh sách Đơn hàng (Đang phát triển)", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFakeData() {
        // Dữ liệu giả lập tạm thời
        tvAdminRevenue.setText("12,500,000 đ");
        tvAdminOrders.setText("45");
    }
}