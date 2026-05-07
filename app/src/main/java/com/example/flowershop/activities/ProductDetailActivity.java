package com.example.flowershop.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.FavoriteItem;
import com.example.flowershop.model.SupabaseFlower;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductBackground;
    private TextView tvProductName, tvProductStock, tvProductDescription, tvProductPrice;
    private ImageButton btnBack, btnFavorite;
    private SupabaseFlower flower;
    private boolean isFavorite = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mAuth = FirebaseAuth.getInstance();
        initViews();
        getData();
        displayData();
        checkFavoriteStatus();

        btnBack.setOnClickListener(v -> finish());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void initViews() {
        ivProductBackground = findViewById(R.id.ivProductBackground);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductStock = findViewById(R.id.tvProductStock);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
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

            if (ivProductBackground != null) {
                try {
                    String imageName = (flower.imageResource != null) ? flower.imageResource.trim() : "default";
                    if (!imageName.toLowerCase().endsWith(".png")) {
                        imageName += ".png";
                    }
                    String path = "flower_image/" + imageName;
                    InputStream is = getAssets().open(path);
                    Drawable d = Drawable.createFromStream(is, null);
                    ivProductBackground.setImageDrawable(d);
                    is.close();
                } catch (Exception e) {
                    try {
                        InputStream isDefault = getAssets().open("flower_image/default.png");
                        Drawable dDefault = Drawable.createFromStream(isDefault, null);
                        ivProductBackground.setImageDrawable(dDefault);
                        isDefault.close();
                    } catch (Exception ex) {
                        ivProductBackground.setImageResource(R.drawable.logo_flower);
                    }
                }
            }
        }
    }

    private void checkFavoriteStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || flower == null) return;

        SupabaseClient.getApi().getFavoritesByUserId("eq." + user.getUid()).enqueue(new Callback<List<FavoriteItem>>() {
            @Override
            public void onResponse(Call<List<FavoriteItem>> call, Response<List<FavoriteItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (FavoriteItem item : response.body()) {
                        if (item.getFlower_id() == flower.id) {
                            isFavorite = true;
                            updateFavoriteUI();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteItem>> call, Throwable t) {
                Log.e("FAVORITE_CHECK", t.getMessage());
            }
        });
    }

    private void toggleFavorite() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để yêu thích!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            SupabaseClient.getApi().deleteFavoriteItem("eq." + user.getUid(), "eq." + flower.id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavorite = false;
                        updateFavoriteUI();
                        Toast.makeText(ProductDetailActivity.this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("user_id", user.getUid());
            data.put("flower_id", flower.id);

            SupabaseClient.getApi().addToFavorite(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isFavorite = true;
                        updateFavoriteUI();
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFavoriteUI() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_heart);
            btnFavorite.setColorFilter(getResources().getColor(R.color.pink_primary));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_heart_outline);
            btnFavorite.setColorFilter(getResources().getColor(R.color.gray_500));
        }
    }
}
