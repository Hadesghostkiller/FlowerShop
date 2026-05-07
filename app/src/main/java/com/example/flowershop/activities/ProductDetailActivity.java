package com.example.flowershop.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.example.flowershop.model.SupabaseFlower;

import java.io.InputStream;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductBackground;
    private TextView tvProductName, tvProductStock, tvProductDescription, tvProductPrice;
    private ImageButton btnBack;
    private SupabaseFlower flower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        getData();
        displayData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        ivProductBackground = findViewById(R.id.ivProductBackground);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductStock = findViewById(R.id.tvProductStock);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        btnBack = findViewById(R.id.btnBack);
    }

    private void getData() {
        flower = (SupabaseFlower) getIntent().getSerializableExtra("flower");
    }

    private void displayData() {
        if (flower != null) {
            tvProductName.setText(flower.flowerName);
            tvProductStock.setText("Số lượng: " + flower.stock);
            tvProductDescription.setText(flower.note != null && !flower.note.isEmpty() ? flower.note : "Không có mô tả sản phẩm.");
            tvProductPrice.setText(String.format("%.0f VND", flower.price));

            try {
                String path = "flower_image/" + flower.imageResource + ".png";
                InputStream is = getAssets().open(path);
                Drawable d = Drawable.createFromStream(is, null);
                ivProductBackground.setImageDrawable(d);
                is.close();
            } catch (Exception e) {
                ivProductBackground.setImageResource(R.drawable.logo_flower);
            }
        }
    }
}
