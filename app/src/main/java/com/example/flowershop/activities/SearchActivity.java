package com.example.flowershop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.R;
import com.example.flowershop.database.FlowerDatabase;
import com.example.flowershop.database.entity.Cart;
import com.example.flowershop.database.entity.Flower;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private FlowerDatabase database;
    private String username;
    private RecyclerView recyclerView;
    private FlowerAdapter adapter;
    private EditText etSearch, etQuantity, etMessage;
    private TextView tvTitle, tvCartInfo;
    private List<Flower> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "user1";

        database = FlowerDatabase.getDatabase(getApplicationContext());

        initViews();
        setupRecyclerView();
        setupListeners();
        loadAllFlowers();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        etQuantity = findViewById(R.id.etQuantity);
        etMessage = findViewById(R.id.etMessage);
        tvTitle = findViewById(R.id.tvTitle);
        tvCartInfo = findViewById(R.id.tvCartInfo);
        recyclerView = findViewById(R.id.recyclerView);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new FlowerAdapter(flower -> {
            addToCart(flower);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        btnSearch.setOnClickListener(v -> search());
        btnAddToCart.setOnClickListener(v -> addSelectedToCart());
    }

    private void loadAllFlowers() {
        new Thread(() -> {
            List<Flower> flowers = database.flowerDao().getAllFlowersSync();
            if (flowers == null || flowers.isEmpty()) {
                database.populateInitialData();
                flowers = database.flowerDao().getAllFlowersSync();
            }
            searchResults = flowers;
            
            runOnUiThread(() -> {
                tvTitle.setText("Tat ca san pham:");
                adapter.setFlowers(searchResults);
                updateCartInfo();
            });
        }).start();
    }

    private void search() {
        String keyword = etSearch.getText().toString().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            loadAllFlowers();
            return;
        }

        new Thread(() -> {
            List<Flower> allFlowers = database.flowerDao().getAllFlowersSync();
            List<Flower> results = new ArrayList<>();
            
            for (Flower f : allFlowers) {
                if (f.flowerName.toLowerCase().contains(keyword)) {
                    results.add(f);
                }
            }
            
            searchResults = results;
            
            runOnUiThread(() -> {
                tvTitle.setText("Ket qua tim kiem: " + results.size() + " san pham");
                adapter.setFlowers(results);
            });
        }).start();
    }

    private void addToCart(Flower flower) {
        int quantity;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString());
            if (quantity <= 0) quantity = 1;
        } catch (Exception e) {
            quantity = 1;
        }

        final int qty = quantity;

        new Thread(() -> {
            // Check if item exists in cart
            List<Cart> carts = database.cartDao().getCartByUsernameSync(username);
            boolean found = false;
            
            if (carts != null) {
                for (Cart c : carts) {
                    if (c.flowerID == flower.flowerID) {
                        database.cartDao().updateQuantity(c.cartID, c.quantity + qty);
                        found = true;
                        break;
                    }
                }
            }
            
            if (!found) {
                Cart cart = new Cart(username, flower.flowerID, qty);
                database.cartDao().insert(cart);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Da them " + qty + " " + flower.flowerName + " vao gio hang!", Toast.LENGTH_SHORT).show();
                updateCartInfo();
            });
        }).start();
    }

    private void addSelectedToCart() {
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "Khong co san pham!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add first item in list (or could be a selected item)
        addToCart(searchResults.get(0));
    }

    private void updateCartInfo() {
        new Thread(() -> {
            List<Cart> carts = database.cartDao().getCartByUsernameSync(username);
            int count = 0;
            if (carts != null) {
                for (Cart c : carts) {
                    count += c.quantity;
                }
            }
            final int finalCount = count;
            runOnUiThread(() -> tvCartInfo.setText("Gio hang: " + finalCount + " san pham"));
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartInfo();
    }
}