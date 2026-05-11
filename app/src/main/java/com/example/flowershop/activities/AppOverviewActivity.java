package com.example.flowershop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;

public class AppOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_overview);

        ImageView btnBack = findViewById(R.id.btnBack);
        LinearLayout layoutDiscord = findViewById(R.id.layoutDiscord);
        TextView tvContactPhone = findViewById(R.id.tvContactPhone);
        TextView tvContactEmail = findViewById(R.id.tvContactEmail);

        btnBack.setOnClickListener(v -> finish());

        layoutDiscord.setOnClickListener(v -> {
            String url = "https://github.com/Hadesghostkiller/FlowerShop.git";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        tvContactPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0376214689"));
            startActivity(intent);
        });

        tvContactEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:imtheone.tellmeurname@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ Flower App");
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
