package com.example.flowershop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.CartAdapter;
import com.example.flowershop.api.SupabaseApi;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty, tvTotalItems, tvTotalPrice;
    private Button btnCheckout;
    private ImageButton btnBack;

    // Đảm bảo khai báo ở đây, chữ 'c' viết thường
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private String currentUserId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCartData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvCart);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        cartItemList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo đối tượng cartAdapter
        cartAdapter = new CartAdapter(this, cartItemList);
        recyclerView.setAdapter(cartAdapter);

        btnBack.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void loadCartData() {
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText("Đang tải giỏ hàng...");

        SupabaseApi api = SupabaseClient.getApi();
        String queryUserId = "eq." + currentUserId;

        api.getCartByUserId(queryUserId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                runOnUiThread(() -> {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            cartItemList.clear();
                            cartItemList.addAll(response.body());

                            // Gọi từ đối tượng chữ 'c' viết thường
                            cartAdapter.notifyDataSetChanged();

                            if (cartItemList.isEmpty()) {
                                tvEmpty.setText("Giỏ hàng của bạn đang trống");
                                tvEmpty.setVisibility(View.VISIBLE);
                                if (tvTotalPrice != null) tvTotalPrice.setText("0 VND");
                                if (tvTotalItems != null) tvTotalItems.setText("0");
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                calculateTotal();
                            }
                        } else {
                            tvEmpty.setText("Lỗi từ server: " + response.code());
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e("CART_CRASH", "Lỗi hiển thị: ", e);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                runOnUiThread(() -> {
                    tvEmpty.setText("Lỗi kết nối: \n" + t.getMessage());
                    tvEmpty.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void calculateTotal() {
        try {
            int totalItems = 0;
            double totalPrice = 0;

            for (CartItem item : cartItemList) {
                totalItems += item.getQuantity();

                if (item.getFlowers() != null) {
                    try {
                        totalPrice += (item.getQuantity() * item.getFlowers().price);
                    } catch (Exception e) {
                        Log.e("CART_CRASH", "Lỗi tính giá tiền: " + e.getMessage());
                    }
                }
            }

            if (tvTotalItems != null) {
                tvTotalItems.setText(String.valueOf(totalItems));
            }

            if (tvTotalPrice != null) {
                tvTotalPrice.setText(String.format("%,.0f VND", totalPrice));
            }

        } catch (Exception e) {
            Log.e("CART_CRASH", "Lỗi văng app ở calculateTotal: ", e);
        }
    }

    private void checkout() {
        if (cartItemList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Chức năng thanh toán đang phát triển!", Toast.LENGTH_SHORT).show();
    }
}