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

        // Load splash image with proper scaling to avoid "too large bitmap" error
        loadSplashImage(imgLogo);

        // Sync data from Supabase to local SQLite
        com.example.flowershop.sync.SupabaseSync.syncFlowers(this);

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

        // 2. Đợi 2 giây để người dùng thấy thương hiệu rồi mới chuyển màn hình
        new Handler().postDelayed(this::checkLoginStatus, 2000);
    }

    private void loadSplashImage(ImageView imageView) {
        try {
            // Get resource ID
            int resId = getResources().getIdentifier("ic_splash", "drawable", getPackageName());
            if (resId != 0) {
                // Decode with sampling to reduce memory
                android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
                options.inSampleSize = 2; // Scale down by 2x
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(
                        getResources(), resId, options);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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