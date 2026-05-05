package com.example.flowershop.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flowershop.R;
import com.example.flowershop.adapters.FlowerAdapter;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.SupabaseFlower;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private RecyclerView recyclerView;
    private TextView tvTitle;
    private ImageButton btnBack;
    private List<SupabaseFlower> allFlowers = new ArrayList<>();
    private FlowerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
        setupRecyclerView();
        fetchData();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        // Sử dụng layout item_flower_search (dạng danh sách, không ảnh)
        adapter = new FlowerAdapter(
                flower -> Toast.makeText(this, "Đã thêm " + flower.flowerName, Toast.LENGTH_SHORT).show(),
                R.layout.item_flower_search
        );
        // Thay đổi sang LinearLayoutManager để hiển thị dạng danh sách
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void fetchData() {
        SupabaseClient.getApi().getFlowers().enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFlowers = response.body();
                    showDefaultItems();
                }
            }
            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDefaultItems() {
        tvTitle.setText("Mặt hàng điển hình");
        List<SupabaseFlower> defaultList = new ArrayList<>();
        int limit = Math.min(10, allFlowers.size());
        for (int i = 0; i < limit; i++) {
            defaultList.add(allFlowers.get(i));
        }
        adapter.setFlowersFromSupabase(defaultList);
    }

    private void filter(String text) {
        if (text.isEmpty()) {
            showDefaultItems();
            return;
        }
        List<SupabaseFlower> filteredList = new ArrayList<>();
        for (SupabaseFlower item : allFlowers) {
            if (item.flowerName.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            tvTitle.setText("Không tìm thấy mặt hàng phù hợp");
        } else {
            tvTitle.setText("Kết quả tìm kiếm cho: " + text);
        }
        adapter.setFlowersFromSupabase(filteredList);
    }
}