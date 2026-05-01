package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    private String username;
    private String fullname;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // 1. Cấu hình Google Sign-In để có thể đăng xuất
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 2. Nhận dữ liệu từ Intent
        username = getIntent().getStringExtra("username");
        fullname = getIntent().getStringExtra("fullname");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Xin chào, " + (fullname != null ? fullname : "bạn") + "!");

        // --- Xử lý sự kiện Click ---

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.tvLogout).setOnClickListener(v -> {
            performLogout();
        });

        // Các nút chức năng khác
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

    /**
     * Logic đăng xuất tổng hợp: Firebase, Google và Facebook
     */
    private void performLogout() {
        // 1. Đăng xuất khỏi Firebase
        FirebaseAuth.getInstance().signOut();

        // 2. Đăng xuất khỏi Google (Xóa session để lần sau hiện bảng chọn tài khoản)
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Sau khi Google sign out xong thì làm tiếp Facebook
            // 3. Đăng xuất khỏi Facebook
            LoginManager.getInstance().logOut();

            // Thông báo và quay về màn hình Login
            Toast.makeText(MenuActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            // Xóa sạch stack các Activity cũ để không thể nhấn Back quay lại Menu
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}