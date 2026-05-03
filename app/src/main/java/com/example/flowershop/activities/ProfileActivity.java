package com.example.flowershop.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack, btnEditProfile;
    private CardView btnEditAvatar;
    private TextView tvFullName, tvEmail, tvPhone, tvDob;
    private View menuLogout, menuSettings; // Khai báo thêm menuSettings

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private final String appId = "default-app-id";

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        saveImageLocally(uri); // GIỮ NGUYÊN LOGIC LOCAL
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        userId = user.getUid();

        initViews();
        loadUser();
        setupListeners();
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvDob = findViewById(R.id.tvDob);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.btnBack);
        menuLogout = findViewById(R.id.menuLogout);
        menuSettings = findViewById(R.id.menuSettings); // Ánh xạ nút cài đặt
    }

    // ================= LOAD USER (GIỮ NGUYÊN LOGIC) =================
    private void loadUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            if (user.getDisplayName() != null)
                tvFullName.setText(user.getDisplayName());
        }

        // Load ảnh local
        File localFile = new File(getFilesDir(), "avatar_" + userId + ".jpg");
        if (localFile.exists()) {
            Glide.with(this)
                    .load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgAvatar);
        }

        // Load Firestore
        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String phone = doc.getString("phone");
                        String dob = doc.getString("dob");
                        String name = doc.getString("name");

                        tvPhone.setText(
                                (phone == null || phone.trim().isEmpty())
                                        ? "Chưa cập nhật SĐT"
                                        : phone
                        );

                        tvDob.setText("Ngày sinh: " +
                                ((dob == null || dob.trim().isEmpty())
                                        ? "chưa cập nhật"
                                        : dob));

                        if (name != null && !name.trim().isEmpty())
                            tvFullName.setText(name);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE_ERROR", e.getMessage()));
    }

    // ================= LƯU ẢNH LOCAL (GIỮ NGUYÊN LOGIC) =================
    private void saveImageLocally(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "avatar_" + userId + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgAvatar);

            Toast.makeText(this, "Đã cập nhật ảnh (Lưu trong máy)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("LOCAL_STORAGE", "Lỗi: " + e.getMessage());
        }
    }

    private void showDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Chỉnh sửa thông tin");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        EditText name = new EditText(this);
        name.setHint("Họ và tên");
        name.setText(tvFullName.getText().toString());

        EditText phone = new EditText(this);
        phone.setHint("Số điện thoại");
        String currentPhone = tvPhone.getText().toString();
        if (!currentPhone.contains("Chưa")) phone.setText(currentPhone);

        EditText dob = new EditText(this);
        dob.setHint("Ngày sinh");
        dob.setFocusable(false);

        String currentDob = tvDob.getText().toString();
        if (currentDob.contains(": ")) {
            dob.setText(currentDob.split(": ")[1]);
        }

        dob.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (d, y, m, day) ->
                    dob.setText(day + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });

        layout.addView(name);
        layout.addView(phone);
        layout.addView(dob);
        b.setView(layout);

        b.setPositiveButton("Lưu", (d, w) ->
                saveInfo(
                        name.getText().toString().trim(),
                        phone.getText().toString().trim(),
                        dob.getText().toString().trim()
                )
        );

        b.setNegativeButton("Hủy", null);
        b.show();
    }

    private void setupListeners() {
        btnEditAvatar.setOnClickListener(v -> openGallery());
        btnBack.setOnClickListener(v -> finish());
        btnEditProfile.setOnClickListener(v -> showDialog());

        // BẤM VÀO CÀI ĐẶT
        if (menuSettings != null) {
            menuSettings.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        menuLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // ================= SAVE FIRESTORE (GIỮ NGUYÊN) =================
    private void saveInfo(String name, String phone, String dob) {
        Map<String, Object> map = new HashMap<>();
        if (!name.isEmpty()) map.put("name", name);
        if (!phone.isEmpty()) map.put("phone", phone);
        if (!dob.isEmpty()) map.put("dob", dob);

        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .set(map, SetOptions.merge())
                .addOnSuccessListener(a -> {
                    tvFullName.setText(name.isEmpty() ? tvFullName.getText() : name);
                    tvPhone.setText(phone.isEmpty() ? "Chưa cập nhật SĐT" : phone);
                    tvDob.setText("Ngày sinh: " + (dob.isEmpty() ? "chưa cập nhật" : dob));
                    Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_SAVE_ERROR", e.getMessage());
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}