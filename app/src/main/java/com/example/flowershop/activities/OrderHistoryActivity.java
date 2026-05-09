package com.example.flowershop.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.OrderAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.Order;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvNoOrders;

    private OrderAdapter adapter;
    private final List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đồng bộ màu Status Bar với giao diện
        setupStatusBar();

        setContentView(R.layout.activity_order_history);

        initViews();
        setupRecyclerView();
        setupListeners();

        loadOrders();
    }

    private void setupStatusBar() {
        Window window = getWindow();

        // Cho status bar màu hồng giống app
        window.setStatusBarColor(Color.parseColor("#E85D9E"));

        // Icon trắng trên status bar
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());

        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }

        // Android cũ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        tvNoOrders = findViewById(R.id.tvNoOrders);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderAdapter(orderList);
        rvOrders.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrders() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        String query = "eq." + userId;

        progressBar.setVisibility(View.VISIBLE);
        tvNoOrders.setVisibility(View.GONE);
        rvOrders.setVisibility(View.GONE);

        SupabaseClient.getApi().getOrders(query)
                .enqueue(new Callback<List<Order>>() {

                    @Override
                    public void onResponse(Call<List<Order>> call,
                                           Response<List<Order>> response) {

                        if (isFinishing()) return;

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {

                            orderList.clear();
                            orderList.addAll(response.body());

                            if (orderList.isEmpty()) {

                                tvNoOrders.setVisibility(View.VISIBLE);
                                rvOrders.setVisibility(View.GONE);

                            } else {

                                tvNoOrders.setVisibility(View.GONE);
                                rvOrders.setVisibility(View.VISIBLE);

                                adapter.notifyDataSetChanged();
                            }

                        } else {

                            tvNoOrders.setVisibility(View.VISIBLE);
                            tvNoOrders.setText("Không thể tải lịch sử đơn hàng");

                            Toast.makeText(
                                    OrderHistoryActivity.this,
                                    "Lỗi tải dữ liệu",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Log.e("ORDER_HISTORY",
                                    "Response error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {

                        if (isFinishing()) return;

                        progressBar.setVisibility(View.GONE);

                        tvNoOrders.setVisibility(View.VISIBLE);
                        tvNoOrders.setText("Lỗi kết nối máy chủ");

                        Toast.makeText(
                                OrderHistoryActivity.this,
                                "Lỗi kết nối",
                                Toast.LENGTH_SHORT
                        ).show();

                        Log.e("ORDER_HISTORY",
                                "Error: " + t.getMessage());
                    }
                });
    }
}