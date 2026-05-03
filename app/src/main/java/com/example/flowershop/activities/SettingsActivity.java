package com.example.flowershop.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchGradient, switchDark;
    private LinearLayout settingsLayout;
    private FirebaseFirestore db;
    private String userId;
    private final String appId = "default-app-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        initViews();
        loadCurrentTheme();
        setupListeners();
    }

    private void initViews() {
        settingsLayout = findViewById(R.id.settingsLayout);
        switchGradient = findViewById(R.id.switchGradient);
        switchDark = findViewById(R.id.switchDark);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        // Xử lý khi nhấn Switch Gradient
        switchGradient.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchDark.setChecked(false);
                updateThemeOnFirestore("gradient");
            } else if (!switchDark.isChecked()) {
                updateThemeOnFirestore("default");
            }
        });

        // Xử lý khi nhấn Switch Dark Mode
        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchGradient.setChecked(false);
                updateThemeOnFirestore("dark");
            } else if (!switchGradient.isChecked()) {
                updateThemeOnFirestore("default");
            }
        });
    }

    private void loadCurrentTheme() {
        if (userId == null) return;

        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("themeMode")) {
                        String theme = doc.getString("themeMode");
                        applyThemeUI(theme);
                    }
                });
    }

    private void updateThemeOnFirestore(String themeMode) {
        Map<String, Object> data = new HashMap<>();
        data.put("themeMode", themeMode);

        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    applyThemeUI(themeMode);
                    Toast.makeText(this, "Đã cập nhật giao diện!", Toast.LENGTH_SHORT).show();
                });
    }

    private void applyThemeUI(String theme) {
        if ("dark".equals(theme)) {
            settingsLayout.setBackgroundColor(android.graphics.Color.parseColor("#121212"));
            switchDark.setChecked(true);
            switchGradient.setChecked(false);
        } else if ("gradient".equals(theme)) {
            settingsLayout.setBackgroundResource(R.drawable.bg_gradient);
            switchGradient.setChecked(true);
            switchDark.setChecked(false);
        } else {
            settingsLayout.setBackgroundColor(android.graphics.Color.WHITE);
            switchGradient.setChecked(false);
            switchDark.setChecked(false);
        }
    }
}