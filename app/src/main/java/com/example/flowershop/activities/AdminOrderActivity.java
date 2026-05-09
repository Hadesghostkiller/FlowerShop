package com.example.flowershop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.AdminOrderAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderActivity extends AppCompatActivity {

    private RecyclerView rvAdminOrders;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private AdminOrderAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo bạn có file layout activity_admin_order.xml tương ứng
        setContentView(R.layout.activity_admin_order);

        initViews();
        setupRecyclerView();
        loadAllOrders();
    }

    private void initViews() {
        rvAdminOrders = findViewById(R.id.rvAdminOrders);
        btnBack = findViewById(R.id.btnBackAdminOrder);
        progressBar = findViewById(R.id.pbAdminOrder);
        tvNoData = findViewById(R.id.tvNoAdminOrders);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(allOrders);
        rvAdminOrders.setAdapter(adapter);
    }

    /**
     * Tải toàn bộ đơn hàng từ bảng 'order' trên Supabase
     */
    private void loadAllOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        // Gọi API lấy toàn bộ đơn hàng (không truyền user_id để lấy hết)
        SupabaseClient.getApi().getOrders(null).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    allOrders.addAll(response.body());

                    if (allOrders.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();
                    Log.d("ADMIN_ORDER", "Đã tải " + allOrders.size() + " đơn hàng.");
                } else {
                    Toast.makeText(AdminOrderActivity.this, "Lỗi tải dữ liệu admin", Toast.LENGTH_SHORT).show();
                    Log.e("ADMIN_ORDER", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminOrderActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                Log.e("ADMIN_ORDER", "Failure: " + t.getMessage());
            }
        });
    }
}