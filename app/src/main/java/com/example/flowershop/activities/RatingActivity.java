package com.example.flowershop.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private RadioGroup rgFlowerQuality;
    private EditText etFeedback;
    private Button btnSubmit;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        initViews();
        setupListeners();
    }

    private void initViews() {
        ratingBar = findViewById(R.id.ratingBar);
        rgFlowerQuality = findViewById(R.id.rgFlowerQuality);
        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        // ĐẶT MÀU VÀNG CHO 5 SAO (Dùng code Java để đảm bảo luôn có màu vàng)
        if (ratingBar != null) {
            ratingBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFD700"))); // Màu Gold/Vàng
            ratingBar.setSecondaryProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFD700")));
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = rgFlowerQuality.getCheckedRadioButtonId();
            String quality = "Chưa chọn";
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                quality = rb.getText().toString();
            }

            String feedback = etFeedback.getText().toString().trim();

            // Gửi đánh giá giả lập (Thành công luôn, không lưu Firestore)
            submitRatingOffline(rating);
        });
    }

    /**
     * Logic thông báo thành công tức thì
     */
    private void submitRatingOffline(float star) {
        // Thông báo thành công cho người dùng thấy
        Toast.makeText(RatingActivity.this,
                "Cảm ơn bạn đã đánh giá " + star + " sao cho Tiệm Hoa!",
                Toast.LENGTH_LONG).show();

        // Đóng màn hình và quay lại Profile ngay lập tức
        finish();
    }
}