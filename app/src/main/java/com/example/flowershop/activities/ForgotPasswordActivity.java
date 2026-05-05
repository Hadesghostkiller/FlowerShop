package com.example.flowershop.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnReset;
    private ImageView btnBack;
    private View loadingOverlay;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ views
        etEmail = findViewById(R.id.etForgotEmail);
        btnReset = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());

        // Sự kiện gửi email
        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            sendResetEmail(email);
        });
    }

    private void sendResetEmail(String email) {
        loadingOverlay.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    loadingOverlay.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Liên kết đặt lại mật khẩu đã được gửi vào email của bạn!",
                                Toast.LENGTH_LONG).show();
                        // Quay lại màn hình đăng nhập sau khi gửi thành công
                        finish();
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}