package com.example.flowershop.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

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
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack, btnEditProfile;
    private CardView btnEditAvatar;
    private TextView tvFullName, tvEmail, tvPhone, tvDob;
    private View menuLogout, menuSettings, menuRating; // Thêm menuRating

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private Uri cameraImageUri;

    private final String appId = "default-app-id";

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        saveImageLocally(uri);
                    }
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraImageUri != null) {
                    saveImageLocally(cameraImageUri);
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadUser();
        }
    }

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
        menuSettings = findViewById(R.id.menuSettings);
        menuRating = findViewById(R.id.menuRating); // Ánh xạ menuRating
    }

    private void loadUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            if (user.getDisplayName() != null)
                tvFullName.setText(user.getDisplayName());
        }

        File localFile = new File(getFilesDir(), "avatar_" + userId + ".jpg");
        if (localFile.exists()) {
            Glide.with(this)
                    .load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgAvatar);
        }

        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String phone = doc.getString("phone");
                        String dob = doc.getString("dob");
                        String name = doc.getString("name");

                        tvPhone.setText((phone == null || phone.trim().isEmpty()) ? "Cập nhật" : phone);
                        tvDob.setText("Ngày sinh: " +
                                ((dob == null || dob.trim().isEmpty()) ? "dd/mm/yyyy" : dob));

                        if (name != null && !name.trim().isEmpty())
                            tvFullName.setText(name);
                    } else {
                        tvPhone.setText("Cập nhật");
                        tvDob.setText("Ngày sinh: dd/mm/yyyy");
                    }
                })
                .addOnFailureListener(e -> Log.e("FIRESTORE_ERROR", e.getMessage()));
    }

    private void setupListeners() {
        btnEditAvatar.setOnClickListener(v -> showImageSourceDialog());
        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("name", tvFullName.getText().toString());
            intent.putExtra("phone", tvPhone.getText().toString());
            intent.putExtra("dob", tvDob.getText().toString());
            startActivityForResult(intent, 100);
        });

        // Xử lý mở màn hình Đánh giá
        if (menuRating != null) {
            menuRating.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, RatingActivity.class);
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

    private void showImageSourceDialog() {
        String[] options = {"Chụp ảnh mới", "Chọn từ thư viện"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật ảnh đại diện");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) openCamera();
            else openGallery();
        });
        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void openCamera() {
        File tempFile = new File(getExternalCacheDir(), "temp_camera_image.jpg");
        cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", tempFile);
        cameraLauncher.launch(cameraImageUri);
    }

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

            Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("LOCAL_STORAGE", "Lỗi: " + e.getMessage());
        }
    }
}