package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.R;
import com.example.flowershop.database.FlowerDatabase;
import com.example.flowershop.database.entity.Flower;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FlowerAdapter.OnAddToCartListener {

    private FlowerDatabase database;
    private String username = "user1";
    private RecyclerView recyclerView;
    private FlowerAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvCartCount;

    private List<Flower> allFlowers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "user1";

        database = FlowerDatabase.getDatabase(getApplicationContext());

        initViews();
        setupRecyclerView();
        setupCategoryButtons();
        loadFlowers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvCartCount = findViewById(R.id.tvCartCount);

        tvCartCount.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new FlowerAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupCategoryButtons() {
        findViewById(R.id.btnAll).setOnClickListener(v -> filterFlowers(""));
        findViewById(R.id.btnHoaBo).setOnClickListener(v -> filterFlowers("Hoa Bo"));
        findViewById(R.id.btnHoaSinhNhat).setOnClickListener(v -> filterFlowers("Sinh Nhat"));
        findViewById(R.id.btnHoaKhaiTruong).setOnClickListener(v -> filterFlowers("Khai Truong"));
        findViewById(R.id.btnHoaChiaBuon).setOnClickListener(v -> filterFlowers("Chia Buon"));
    }

    private void loadFlowers() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                database.populateInitialData();

                List<Flower> checkFlowers = database.flowerDao().getAllFlowersSync();

                allFlowers = new ArrayList<>();
                if (checkFlowers != null && !checkFlowers.isEmpty()) {
                    allFlowers.addAll(checkFlowers);
                }

                final List<Flower> finalFlowers = allFlowers;
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.setFlowers(finalFlowers);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void filterFlowers(String category) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            List<Flower> filtered;

            if (category.isEmpty()) {
                // Show all flowers
                filtered = new ArrayList<>(allFlowers);
            } else {
                // Filter by category - query directly from database
                filtered = database.flowerDao().getFlowersByCategorySync(category);

                // If DAO method doesn't exist, filter from cached list
                if (filtered == null || filtered.isEmpty()) {
                    filtered = new ArrayList<>();
                    for (Flower f : allFlowers) {
                        if (f.category != null && f.category.equals(category)) {
                            filtered.add(f);
                        }
                    }
                }
            }

            final List<Flower> result = filtered;
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.setFlowers(result);
            });
        }).start();
    }

    @Override
    public void onAddToCart(Flower flower) {
        new Thread(() -> {
            FlowerDatabase db = FlowerDatabase.getDatabase(getApplicationContext());
            List<com.example.flowershop.database.entity.Cart> carts = db.cartDao().getCartByUsernameSync(username);

            boolean found = false;
            if (carts != null) {
                for (com.example.flowershop.database.entity.Cart c : carts) {
                    if (c.flowerID == flower.flowerID) {
                        db.cartDao().updateQuantity(c.cartID, c.quantity + 1);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                com.example.flowershop.database.entity.Cart cart =
                    new com.example.flowershop.database.entity.Cart(username, flower.flowerID, 1);
                db.cartDao().insert(cart);
            }

            runOnUiThread(() -> updateCartCount());
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    private void updateCartCount() {
        new Thread(() -> {
            FlowerDatabase db = FlowerDatabase.getDatabase(getApplicationContext());
            List<com.example.flowershop.database.entity.Cart> carts = db.cartDao().getCartByUsernameSync(username);
            int count = 0;
            if (carts != null) {
                for (com.example.flowershop.database.entity.Cart c : carts) {
                    count += c.quantity;
                }
            }
            final int finalCount = count;
            runOnUiThread(() -> tvCartCount.setText(String.format(Locale.getDefault(), "Gio hang (%d)", finalCount)));
        }).start();
    }
}