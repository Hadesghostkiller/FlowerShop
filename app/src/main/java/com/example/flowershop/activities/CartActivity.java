package com.example.flowershop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty, tvTotalItems, tvTotalPrice;
    private Button btnCheckout;
    private ImageButton btnBack;
    private String username = "user1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "user1";

        initViews();
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
        
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText("Chuc nang gio hang dang phat trien...");
    }

    private void checkout() {
        Toast.makeText(this, "Chuc nang gio hang dang phat trien!", Toast.LENGTH_SHORT).show();
    }
}