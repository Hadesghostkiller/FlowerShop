package com.example.flowershop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity {

    private EditText etGmail, etUsername, etPassword;
    private TextView btnSignUp, tvBackToLogin, tvError;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        initViews();

        btnSignUp.setOnClickListener(v -> {
            animateButtonClick(v);
            registerWithFirebase();
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etGmail = findViewById(R.id.etGmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        tvError = findViewById(R.id.tvError);
    }

    private void registerWithFirebase() {
        String gmail = etGmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra đầu vào
        if (gmail.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Vui lòng không để trống thông tin!");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(gmail).matches()) {
            showErrorMessage("Định dạng Gmail không hợp lệ!");
            return;
        }

        if (password.length() < 6) {
            showErrorMessage("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        tvError.setVisibility(View.GONE);

        // Bắt đầu tạo tài khoản
        mAuth.createUserWithEmailAndPassword(gmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lưu Username vào DisplayName của Firebase
                        updateDisplayName(username);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Lỗi đăng ký";
                        showErrorMessage(error);
                    }
                });
    }

    private void updateDisplayName(String username) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
    }

    private void showErrorMessage(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void animateButtonClick(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            v.animate().scaleX(1f).scaleY(1f).setDuration(100);
        });
    }
}