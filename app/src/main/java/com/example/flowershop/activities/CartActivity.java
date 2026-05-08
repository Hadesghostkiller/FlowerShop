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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty, tvTotalItems, tvTotalPrice;
    private CheckBox cbSelectAll; // THÊM MỚI
    private Button btnCheckout;
    private ImageButton btnBack;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private boolean isProgrammaticChange = false;
    private double totalPrice = 0;

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

    @Override
    protected void onResume() {
        super.onResume();
        loadCartData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvCart);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        cbSelectAll = findViewById(R.id.cbSelectAll); // Ánh xạ
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        cartItemList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter với 2 Listener (Xóa và Check)
        cartAdapter = new CartAdapter(this, cartItemList,
                itemToDelete -> deleteItemFromCart(itemToDelete),
                (item, isChecked) -> {
                    // Khi tick/bỏ tick 1 item lẻ
                    updateSelectAllCheckboxState();
                    calculateTotal();
                }
        );
        recyclerView.setAdapter(cartAdapter);

        // Sự kiện "Chọn tất cả"
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isProgrammaticChange) return; // Nếu code đang tự đổi state thì bỏ qua

            for (CartItem item : cartItemList) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged(); // Cập nhật lại UI list
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

                            HashMap<Integer, CartItem> mergedMap = new HashMap<>();
                            for (CartItem item : rawList) {
                                // Mặc định khi tải về là được tick
                                item.setSelected(true);

                                int fId = item.getFlower_id();
                                if (mergedMap.containsKey(fId)) {
                                    CartItem existingItem = mergedMap.get(fId);
                                    existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                                } else {
                                    mergedMap.put(fId, item);
                                }
                            }

                            cartItemList.addAll(mergedMap.values());
                            cartAdapter.notifyDataSetChanged();

                            if (cartItemList.isEmpty()) {
                                tvEmpty.setText("Giỏ hàng của bạn đang trống");
                                tvEmpty.setVisibility(View.VISIBLE);
                                cbSelectAll.setVisibility(View.GONE); // Ẩn nút chọn tất cả
                                if (tvTotalPrice != null) tvTotalPrice.setText("0 VND");
                                if (tvTotalItems != null) tvTotalItems.setText("0");
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                cbSelectAll.setVisibility(View.VISIBLE);
                                isProgrammaticChange = true;
                                cbSelectAll.setChecked(true); // Mặc định chọn tất cả
                                isProgrammaticChange = false;
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

    private void deleteItemFromCart(CartItem item) {
        Toast.makeText(this, "Đang xóa...", Toast.LENGTH_SHORT).show();
        SupabaseApi api = SupabaseClient.getApi();

        String qUserId = "eq." + currentUserId;
        String qFlowerId = "eq." + item.getFlower_id();

        api.deleteCartItem(qUserId, qFlowerId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    loadCartData();
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi khi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // THÊM MỚI: Kiểm tra xem tất cả các mục có đang được tick hay không
    private void updateSelectAllCheckboxState() {
        if (cartItemList.isEmpty()) return;
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
        try {
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
        boolean hasSelectedItem = false;
        for (CartItem item : cartItemList) {
            if (item.isSelected()) {
                hasSelectedItem = true;
                break;
            }
        }

        if (cartItemList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasSelectedItem) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("totalAmount", totalPrice);
        intent.putExtra("cartItems", selectedItems);
        startActivity(intent);
    }
}