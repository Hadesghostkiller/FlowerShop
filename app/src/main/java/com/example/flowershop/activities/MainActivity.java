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
import com.example.flowershop.model.SupabaseFlower;
import com.example.flowershop.sync.SupabaseSync;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FlowerAdapter.OnAddToCartListener {

    private String username = "user1";
    private RecyclerView recyclerView;
    private FlowerAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvCartCount;

    private List<SupabaseFlower> allFlowers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "user1";

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

        SupabaseSync.getFlowers(new SupabaseSync.FlowerCallback() {
            @Override
            public void onSuccess(List<SupabaseFlower> flowers) {
                allFlowers = flowers;
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.setFlowersFromSupabase(allFlowers);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void filterFlowers(String category) {
        List<SupabaseFlower> filtered;

        if (category.isEmpty()) {
            filtered = new ArrayList<>(allFlowers);
        } else {
            filtered = new ArrayList<>();
            for (SupabaseFlower f : allFlowers) {
                if (f.category != null && f.category.equals(category)) {
                    filtered.add(f);
                }
            }
        }

        adapter.setFlowersFromSupabase(filtered);
    }

    @Override
    public void onAddToCart(SupabaseFlower flower) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}