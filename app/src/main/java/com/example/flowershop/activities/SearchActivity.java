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
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.sync.SupabaseSync;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private String username;
    private RecyclerView recyclerView;
    private FlowerAdapter adapter;
    private EditText etSearch, etQuantity, etMessage;
    private TextView tvTitle, tvCartInfo;
    private List<SupabaseFlower> allFlowers = new ArrayList<>();
    private List<SupabaseFlower> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "user1";

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
        SupabaseSync.getFlowers(new SupabaseSync.FlowerCallback() {
            @Override
            public void onSuccess(List<SupabaseFlower> flowers) {
                allFlowers = flowers;
                searchResults = flowers;
                runOnUiThread(() -> {
                    tvTitle.setText("Tat ca san pham:");
                    adapter.setFlowersFromSupabase(searchResults);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(SearchActivity.this, "Loi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void search() {
        String keyword = etSearch.getText().toString().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            searchResults = new ArrayList<>(allFlowers);
            tvTitle.setText("Tat ca san pham:");
            adapter.setFlowersFromSupabase(searchResults);
            return;
        }

        List<SupabaseFlower> results = new ArrayList<>();
        
        for (SupabaseFlower f : allFlowers) {
            if (f.flowerName != null && f.flowerName.toLowerCase().contains(keyword)) {
                results.add(f);
            }
        }
        
        searchResults = results;
        
        tvTitle.setText("Ket qua tim kiem: " + results.size() + " san pham");
        adapter.setFlowersFromSupabase(results);
    }

    private void addToCart(SupabaseFlower flower) {
        Toast.makeText(this, "Da chon: " + flower.flowerName, Toast.LENGTH_SHORT).show();
    }

    private void addSelectedToCart() {
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "Khong co san pham!", Toast.LENGTH_SHORT).show();
            return;
        }

        addToCart(searchResults.get(0));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}