package com.example.flowershop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imgLogo = findViewById(R.id.imgSplashLogo);
        TextView tvName = findViewById(R.id.tvSplashName);

        // 1. Tạo hiệu ứng hiện dần mượt mà ngay khi mở app
        if (imgLogo != null && tvName != null) {
            imgLogo.setAlpha(0f);
            tvName.setAlpha(0f);

            imgLogo.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            tvName.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(200) // Tên hiện sau logo một chút cho nghệ thuật
                    .start();
        }

        // Sync data from Supabase
        com.example.flowershop.sync.SupabaseSync.syncFlowers(this);

        // 2. Đợi 2 giây để người dùng thấy thương hiệu rồi mới chuyển màn hình
        new Handler().postDelayed(this::checkLoginStatus, 2000);
    }

    private void checkLoginStatus() {
        // Đây chính là dòng lệnh "đọc" trạng thái đăng nhập đã lưu
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // Đã đăng nhập trước đó -> Vào thẳng Menu
            intent = new Intent(SplashActivity.this, MenuActivity.class);
            // Bạn có thể truyền thông tin người dùng sang Menu nếu muốn
            intent.putExtra("username", currentUser.getEmail());
            intent.putExtra("fullname", currentUser.getDisplayName());
        } else {
            // Chưa đăng nhập hoặc đã logout -> Sang màn hình Login
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);

        // Quan trọng: Đóng Splash để người dùng nhấn Back không bị quay lại đây
        finish();
    }
}