package com.example.flowershop.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack, btnEditProfile;
    private CardView btnEditAvatar;
    private TextView tvFullName, tvEmail, tvPhone, tvDob;
    private View menuLogout, menuSettings, menuRating, menuProducts, menuOrderHistory, menuRegisterSeller, menuInvite;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private Uri cameraImageUri;
    private boolean isSeller = false;

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
                } else {
                    Log.d("CAMERA_LOG", "Chụp ảnh thất bại hoặc người dùng hủy.");
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCameraIntent();
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100 || requestCode == 200) && resultCode == RESULT_OK) {
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
        menuRating = findViewById(R.id.menuRating);
        menuProducts = findViewById(R.id.menuProducts);
        menuOrderHistory = findViewById(R.id.menuOrderHistory);
        menuRegisterSeller = findViewById(R.id.menuRegisterSeller);
        menuInvite = findViewById(R.id.menuInvite);

        // ĐÃ SỬA: Ẩn ngay từ đầu để tránh hiện tượng nháy (flicker)
        if (menuProducts != null) menuProducts.setVisibility(View.INVISIBLE);
        if (menuRegisterSeller != null) menuRegisterSeller.setVisibility(View.GONE);
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
                        Boolean sellerStatus = doc.getBoolean("is_seller");

                        isSeller = (sellerStatus != null && sellerStatus);

                        tvPhone.setText((phone == null || phone.trim().isEmpty()) ? "Cập nhật" : phone);
                        tvDob.setText("Ngày sinh: " +
                                ((dob == null || dob.trim().isEmpty()) ? "dd/mm/yyyy" : dob));

                        if (name != null && !name.trim().isEmpty())
                            tvFullName.setText(name);
                    } else {
                        // Trường hợp user mới chưa có dữ liệu profile
                        isSeller = false;
                        tvPhone.setText("Cập nhật");
                        tvDob.setText("Ngày sinh: dd/mm/yyyy");
                    }

                    // LUÔN LUÔN gọi cập nhật UI sau khi đã xác định được isSeller hay chưa
                    updateSellerUI();
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_ERROR", e.getMessage());
                    updateSellerUI(); // Gọi kể cả khi lỗi để hiện UI mặc định
                });
    }

    private void updateSellerUI() {
        if (menuProducts == null) return;

        // Hiện lại menu Products (vì ở initViews mình để Invisible)
        menuProducts.setVisibility(View.VISIBLE);

        if (isSeller) {
            // Đã là người bán: Hiện rõ nút Quản lý, Ẩn nút Đăng ký
            menuProducts.setAlpha(1.0f);
            menuProducts.setEnabled(true);
            if (menuRegisterSeller != null) menuRegisterSeller.setVisibility(View.GONE);
        } else {
            // Chưa là người bán: Làm mờ nút Quản lý, Hiện nút Đăng ký
            menuProducts.setAlpha(0.3f);
            menuProducts.setEnabled(false);
            if (menuRegisterSeller != null) menuRegisterSeller.setVisibility(View.VISIBLE);
        }
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

        if (menuInvite != null) {
            menuInvite.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AppOverviewActivity.class);
                startActivity(intent);
            });
        }

        if (menuRating != null) {
            menuRating.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, RatingActivity.class);
                startActivity(intent);
            });
        }

        if (menuRegisterSeller != null) {
            menuRegisterSeller.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, RegisterSellerActivity.class);
                startActivityForResult(intent, 200);
            });
        }

        if (menuProducts != null) {
            menuProducts.setOnClickListener(v -> {
                if (isSeller) {
                    Intent intent = new Intent(ProfileActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (menuOrderHistory != null) {
            menuOrderHistory.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraIntent();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCameraIntent() {
        try {
            File storageDir = getExternalCacheDir();
            File tempFile = new File(storageDir, "temp_camera_image.jpg");
            cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", tempFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (Exception e) {
            Log.e("CAMERA_ERROR", "Lỗi: " + e.getMessage());
            Toast.makeText(this, "Không thể mở máy ảnh", Toast.LENGTH_SHORT).show();
        }
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
            Log.e("LOCAL_STORAGE", "Lỗi lưu ảnh: " + e.getMessage());
        }
    }
}