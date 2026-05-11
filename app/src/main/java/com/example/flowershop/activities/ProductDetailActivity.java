package com.example.flowershop.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.FavoriteItem;
import com.example.flowershop.model.SupabaseFlower;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductBackground;
    private TextView tvProductName, tvProductStock, tvProductDescription, tvProductPrice, tvQuantity;
    private ImageButton btnBack, btnFavorite, btnAddQty, btnRemoveQty;
    private MaterialButton btnAddToBag, btnBuyNow, btnSelectDate, btnSelectTime;
    private RelativeLayout layoutCardMessage, layoutSpecialInstructions;
    private EditText etCardMessage, etSpecialInstructions;
    private ImageView ivArrowCard, ivArrowSpecial;
    
    private SupabaseFlower flower;
    private boolean isFavorite = false;
    private FirebaseAuth mAuth;
    private int quantity = 1;
    
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mAuth = FirebaseAuth.getInstance();
        initViews();
        getData();
        
        btnBack.setOnClickListener(v -> finish());
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        btnAddQty.setOnClickListener(v -> {
            if (flower != null && quantity < flower.stock) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Đã đạt giới hạn tồn kho", Toast.LENGTH_SHORT).show();
            }
        });

        btnRemoveQty.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        
        layoutCardMessage.setOnClickListener(v -> {
            if (etCardMessage.getVisibility() == View.GONE) {
                etCardMessage.setVisibility(View.VISIBLE);
                ivArrowCard.setRotation(180);
            } else {
                etCardMessage.setVisibility(View.GONE);
                ivArrowCard.setRotation(0);
            }
        });
        
        layoutSpecialInstructions.setOnClickListener(v -> {
            if (etSpecialInstructions.getVisibility() == View.GONE) {
                etSpecialInstructions.setVisibility(View.VISIBLE);
                ivArrowSpecial.setRotation(180);
            } else {
                etSpecialInstructions.setVisibility(View.GONE);
                ivArrowSpecial.setRotation(0);
            }
        });

        btnAddToBag.setOnClickListener(v -> addToCart(false));
        btnBuyNow.setOnClickListener(v -> addToCart(true));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
            btnSelectDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            btnSelectTime.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProductData();
        checkFavoriteStatus();
    }

    private void initViews() {
        ivProductBackground = findViewById(R.id.ivProductBackground);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductStock = findViewById(R.id.tvProductStock);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAddQty = findViewById(R.id.btnAddQty);
        btnRemoveQty = findViewById(R.id.btnRemoveQty);
        btnAddToBag = findViewById(R.id.btnAddToBag);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        layoutCardMessage = findViewById(R.id.layoutCardMessage);
        layoutSpecialInstructions = findViewById(R.id.layoutSpecialInstructions);
        etCardMessage = findViewById(R.id.etCardMessage);
        etSpecialInstructions = findViewById(R.id.etSpecialInstructions);
        ivArrowCard = findViewById(R.id.ivArrowCard);
        ivArrowSpecial = findViewById(R.id.ivArrowSpecial);
    }

    private void getData() {
        flower = (SupabaseFlower) getIntent().getSerializableExtra("flower");
        displayData();
    }

    private void refreshProductData() {
        if (flower == null) return;
        
        SupabaseClient.getApi().getFlowers().enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SupabaseFlower f : response.body()) {
                        if (f.id == flower.id) {
                            flower = f;
                            displayData();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                Log.e("REFRESH_PRODUCT", t.getMessage());
            }
        });
    }

    private void displayData() {
        if (flower != null) {
            tvProductName.setText(flower.flowerName);
            tvProductStock.setText("Số lượng: " + flower.stock);
            tvProductDescription.setText(flower.note != null && !flower.note.isEmpty() ? flower.note : "Không có mô tả sản phẩm.");
            tvProductPrice.setText(String.format("%,.0f VND", flower.price));

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

    private void addToCart(boolean isBuyNow) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (flower == null || flower.stock <= 0) {
            Toast.makeText(this, "Sản phẩm đã hết hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateTime = selectedDate + (selectedDate.isEmpty() || selectedTime.isEmpty() ? "" : " ") + selectedTime;
        String cardMess = etCardMessage.getText().toString().trim();
        String instructions = etSpecialInstructions.getText().toString().trim();

        Map<String, Object> cartData = new HashMap<>();
        cartData.put("user_id", user.getUid());
        cartData.put("flower_id", flower.id);
        cartData.put("quantity", quantity);
        cartData.put("date_time", dateTime);
        cartData.put("card_mess", cardMess);
        cartData.put("instructions", instructions);

        SupabaseClient.getApi().addToCart(cartData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (isBuyNow) {
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    intent.putExtra("buy_now_flower_id", flower.id);
                    startActivity(intent);
                } else {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Đã cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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