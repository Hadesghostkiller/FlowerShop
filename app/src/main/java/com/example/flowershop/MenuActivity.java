package com.example.flowershop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private String username;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        username = getIntent().getStringExtra("username");
        fullname = getIntent().getStringExtra("fullname");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Xin chao, " + fullname + "!");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.tvLogout).setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            finish();
        });

        Button btnShop = findViewById(R.id.btnShop);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnCart = findViewById(R.id.btnCart);

        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SearchActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CartActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
}