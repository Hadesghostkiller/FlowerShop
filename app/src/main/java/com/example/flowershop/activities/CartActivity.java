package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty, tvTotalItems, tvTotalPrice;
    private CheckBox cbSelectAll;
    private Button btnCheckout;
    private ImageButton btnBack;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private boolean isProgrammaticChange = false;
    private double totalPrice = 0;
    private int buyNowFlowerId = -1;

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

        buyNowFlowerId = getIntent().getIntExtra("buy_now_flower_id", -1);

        initViews();
        loadCartData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại giỏ hàng khi quay lại từ Checkout
        loadCartData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvCart);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        cbSelectAll = findViewById(R.id.cbSelectAll);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        cartItemList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(this, cartItemList,
                this::deleteItemFromCart,
                (item, isChecked) -> {
                    updateSelectAllCheckboxState();
                    calculateTotal();
                },
                this::updateItemQuantity
        );
        recyclerView.setAdapter(cartAdapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isProgrammaticChange) return;

            for (CartItem item : cartItemList) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            calculateTotal();
        });

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
                            List<CartItem> rawList = response.body();
                            cartItemList.clear();

                            // Không thực hiện gộp (merge) nữa để giữ các lời chúc/ghi chú riêng biệt cho từng dòng
                            for (CartItem item : rawList) {
                                item.setSelected(false);
                                if (buyNowFlowerId != -1 && item.getFlower_id() == buyNowFlowerId) {
                                    item.setSelected(true);
                                }
                                cartItemList.add(item);
                            }

                            cartAdapter.notifyDataSetChanged();

                            if (cartItemList.isEmpty()) {
                                tvEmpty.setText("Giỏ hàng của bạn đang trống");
                                tvEmpty.setVisibility(View.VISIBLE);
                                cbSelectAll.setVisibility(View.GONE);
                                updateUI(0, 0);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                cbSelectAll.setVisibility(View.VISIBLE);
                                updateSelectAllCheckboxState();
                                calculateTotal();
                                buyNowFlowerId = -1;
                            }
                        } else {
                            tvEmpty.setText("Lỗi từ server: " + response.code());
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
                });
            }
        });
    }

    private void updateItemQuantity(CartItem item, int newQuantity) {
        SupabaseApi api = SupabaseClient.getApi();
        // Sử dụng ID chính của hàng (row id) để update chính xác thay vì flower_id
        String qId = "eq." + item.getId();

        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);

        api.updateCartQuantity("eq." + currentUserId, "eq." + item.getFlower_id(), updates).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    item.setQuantity(newQuantity);
                    runOnUiThread(() -> {
                        cartAdapter.notifyDataSetChanged();
                        calculateTotal();
                    });
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void deleteItemFromCart(CartItem item) {
        // Xóa dựa trên id duy nhất của cart item
        SupabaseClient.getApi().deleteCartItem("eq." + currentUserId, "eq." + item.getFlower_id())
                .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadCartData();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void updateSelectAllCheckboxState() {
        if (cartItemList.isEmpty()) {
            isProgrammaticChange = true;
            cbSelectAll.setChecked(false);
            isProgrammaticChange = false;
            return;
        }
        boolean allSelected = true;
        for (CartItem item : cartItemList) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }

        isProgrammaticChange = true;
        cbSelectAll.setChecked(allSelected);
        isProgrammaticChange = false;
    }

    private void calculateTotal() {
        int totalItems = 0;
        totalPrice = 0;
        for (CartItem item : cartItemList) {
            if (item.isSelected()) {
                totalItems += item.getQuantity();
                if (item.getFlowers() != null) {
                    totalPrice += (item.getQuantity() * item.getFlowers().price);
                }
            }
        }
        updateUI(totalItems, totalPrice);
    }

    private void updateUI(int items, double price) {
        if (tvTotalItems != null) tvTotalItems.setText(String.valueOf(items));
        if (tvTotalPrice != null) tvTotalPrice.setText(String.format("%,.0f VND", price));
    }

    private void checkout() {
        ArrayList<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (item.isSelected()) selectedItems.add(item);
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("totalAmount", totalPrice);
        intent.putExtra("cartItems", selectedItems);
        startActivity(intent);
    }
}