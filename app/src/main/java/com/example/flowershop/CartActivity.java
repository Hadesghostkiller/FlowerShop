package com.example.flowershop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.database.FlowerDatabase;
import com.example.flowershop.database.entity.Cart;
import com.example.flowershop.database.entity.Flower;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    public static class CartItemWithFlower {
        public Cart cart;
        public Flower flower;

        public CartItemWithFlower(Cart cart, Flower flower) {
            this.cart = cart;
            this.flower = flower;
        }
    }

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView tvEmpty, tvTotalItems, tvTotalPrice;
    private Button btnCheckout;
    private ImageButton btnBack;

private FlowerDatabase database;
    private String username = "user1";
    private List<CartItemWithFlower> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "user1";

        database = FlowerDatabase.getDatabase(getApplicationContext());

        initViews();
        setupRecyclerView();
        loadCart();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvCart);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(cartItems, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged(int cartId, int newQuantity) {
                updateQuantity(cartId, newQuantity);
            }

            @Override
            public void onDelete(int cartId) {
                deleteItem(cartId);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadCart() {
        new Thread(() -> {
            List<Cart> carts = database.cartDao().getCartByUsernameSync(username);
            
            cartItems.clear();
            
            if (carts != null) {
                for (Cart cart : carts) {
                    Flower flower = database.flowerDao().getFlowerById(cart.flowerID);
                    if (flower != null) {
                        cartItems.add(new CartItemWithFlower(cart, flower));
                    }
                }
            }
            
            runOnUiThread(() -> updateUI());
        }).start();
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvTotalItems.setText("0");
            tvTotalPrice.setText("0 VND");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            calculateTotal();
        }
    }

    private void calculateTotal() {
        int totalItems = 0;
        double total = 0;
        for (CartItemWithFlower item : cartItems) {
            totalItems += item.cart.quantity;
            total += item.flower.price * item.cart.quantity;
        }
        tvTotalItems.setText(String.valueOf(totalItems));
        tvTotalPrice.setText(String.format("%.0f VND", total));
    }

    private void updateQuantity(int cartId, int newQuantity) {
        new Thread(() -> {
            if (newQuantity <= 0) {
                Cart cart = database.cartDao().getCartItemSync(cartId);
                if (cart != null) {
                    database.cartDao().delete(cart);
                }
            } else {
                database.cartDao().updateQuantity(cartId, newQuantity);
            }
            loadCart();
        }).start();
    }

    private void deleteItem(int cartId) {
        new Thread(() -> {
            Cart cart = database.cartDao().getCartItemSync(cartId);
            if (cart != null) {
                database.cartDao().delete(cart);
            }
            loadCart();
        }).start();
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Gio hang trong!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total
        final double[] total = {0};
        for (CartItemWithFlower item : cartItems) {
            total[0] += item.flower.price * item.cart.quantity;
        }

        final double finalTotal = total[0];

        // Apply voucher (logic from FlowerShopLogicSystem)
        showVoucherDialog(finalTotal);
    }

    private void showVoucherDialog(double originalTotal) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Ma giam gia");
        builder.setMessage("Nhap ma giam gia (bo qua neu khong co)\nMa giam gia: 4THANGGAY giam 50,000 VND");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nhap ma...");
        builder.setView(input);

        builder.setPositiveButton("Ap dung", (dialog, which) -> {
            String voucher = input.getText().toString().trim();
            double discount = 0;
            
            if (voucher.equalsIgnoreCase("4THANGGAY")) {
                discount = 50000;
                Toast.makeText(this, "Da ap dung ma 4THANGGAY: Giam 50,000 VND", Toast.LENGTH_SHORT).show();
            }

            double finalPrice = originalTotal - discount;
            if (finalPrice < 0) finalPrice = 0;

            showDeliveryOption(originalTotal, discount, finalPrice);
        });

        builder.setNegativeButton("Bo qua", (dialog, which) -> {
            showDeliveryOption(originalTotal, 0, originalTotal);
        });

        builder.show();
    }

    private void showDeliveryOption(double originalTotal, double discount, double finalPrice) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Hinh thuc nhan hang");
        builder.setItems(new String[]{"Tai quay (miễn phí)", "Giao hang tan noi (+35,000 VND)"}, (dialog, which) -> {
            double shipping = (which == 1) ? 35000 : 0;
            double totalWithShipping = finalPrice + shipping;
            
            processCheckout(originalTotal, discount, shipping, totalWithShipping, which == 1 ? "Giao hang tan noi" : "Tai quay");
        });

        builder.show();
    }

    private void processCheckout(double originalTotal, double discount, double shipping, double finalTotal, String deliveryType) {
        final String user = username;
        new Thread(() -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new java.util.Date());

            com.example.flowershop.database.entity.Order order =
                    new com.example.flowershop.database.entity.Order(user, date, finalTotal, "pending");
            database.orderDao().insert(order);

            // Clear cart after checkout
            database.cartDao().clearCart(user);

            runOnUiThread(() -> {
                printInvoice(originalTotal, discount, shipping, finalTotal, deliveryType);
            });
        }).start();
    }

    private void printInvoice(double originalTotal, double discount, double shipping, double finalTotal, String deliveryType) {
        StringBuilder invoice = new StringBuilder();
        invoice.append("====================================\n");
        invoice.append("         HOA DON THANH TOAN          \n");
        invoice.append("====================================\n");
        invoice.append("Hinh thuc: ").append(deliveryType).append("\n");
        
        if (discount > 0) {
            invoice.append("Giam gia: -").append(String.format("%.0f VND", discount)).append("\n");
        }
        
        if (shipping > 0) {
            invoice.append("Phi ship: +").append(String.format("%.0f VND", shipping)).append("\n");
        }
        
        invoice.append("-----------------------------------\n");
        invoice.append("TONG TIEN: ").append(String.format("%.0f VND", finalTotal)).append("\n");
        invoice.append("====================================\n");

        Toast.makeText(this, "Dat hang thanh cong!", Toast.LENGTH_LONG).show();

        // Show invoice in a dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Hoa Don");
        builder.setMessage(invoice.toString());
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.show();
    }
}