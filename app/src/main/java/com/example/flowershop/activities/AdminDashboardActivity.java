package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Tải dữ liệu thống kê thực tế từ Supabase
        loadRealStatistics();
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

        // Mở màn hình Thêm Hoa
        btnUploadProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAddEditProductActivity.class);
            startActivity(intent);
        });

        // Mở màn hình Quản lý Danh mục
        btnManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminManageProductsActivity.class);
            startActivity(intent);
        });

        // Mở màn hình Quản lý Đơn hàng
        btnManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminOrderActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Tải dữ liệu từ bảng 'order' để tính toán tổng đơn và doanh thu
     */
    private void loadRealStatistics() {
        // Truyền null vào query để lấy toàn bộ đơn hàng của tất cả khách hàng
        SupabaseClient.getApi().getOrders(null).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();

                    int totalOrders = orders.size();
                    double totalRevenue = 0;

                    // Lặp qua danh sách đơn hàng để cộng dồn doanh thu
                    for (Order order : orders) {
                        totalRevenue += order.total_amount;
                    }

                    // Hiển thị dữ liệu lên giao diện
                    tvAdminOrders.setText(String.valueOf(totalOrders));
                    // Định dạng hiển thị tiền tệ (ví dụ: 1.000.000 đ)
                    tvAdminRevenue.setText(String.format("%,.0f đ", totalRevenue));

                    Log.d("ADMIN_STATS", "Thành công: " + totalOrders + " đơn hàng.");
                } else {
                    Log.e("ADMIN_STATS", "Lỗi phản hồi: " + response.code());
                    tvAdminOrders.setText("0");
                    tvAdminRevenue.setText("0 đ");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("ADMIN_STATS", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(AdminDashboardActivity.this, "Không thể tải thống kê", Toast.LENGTH_SHORT).show();
            }
        });
    }
}