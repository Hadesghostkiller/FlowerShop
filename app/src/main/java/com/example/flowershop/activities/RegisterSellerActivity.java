package com.example.flowershop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterSellerActivity extends AppCompatActivity {

    private EditText etIDCard, etAddress, etPhone;
    private Button btnSubmit;
    private ImageView btnBack;
    private FirebaseFirestore db;
    private String userId;

    private final String appId = "default-app-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etIDCard = findViewById(R.id.etIDCard);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> handleRegistration());
    }

    private void handleRegistration() {
        String idCard = etIDCard.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (idCard.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> sellerUpdate = new HashMap<>();
        sellerUpdate.put("id_card", idCard);
        sellerUpdate.put("shop_address", address);
        sellerUpdate.put("shop_phone", phone);
        sellerUpdate.put("is_seller", true); // Đánh dấu là người bán

        // Cập nhật Firestore theo đường dẫn đã thống nhất
        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .update(sellerUpdate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK); // Trả về kết quả OK để ProfileActivity load lại
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}