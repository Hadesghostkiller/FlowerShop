package com.example.flowershop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.example.flowershop.api.SupabaseApi;
import com.example.flowershop.api.SupabaseClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddEditProductActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle, tvImageSelected;
    private EditText etName, etPrice;
    private Button btnSave, btnUploadImage;
    private ImageView ivProductPreview;

    private int flowerId = -1;
    private String selectedImageUriString = "";

    // ĐÃ SỬA: Copy ảnh vào thư mục nội bộ của App để không bị mất quyền truy cập
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            File file = new File(getFilesDir(), "flower_" + System.currentTimeMillis() + ".jpg");
                            OutputStream outputStream = new FileOutputStream(file);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            outputStream.flush();
                            outputStream.close();
                            inputStream.close();

                            // Lưu đường dẫn file nội bộ cực kỳ an toàn
                            selectedImageUriString = file.getAbsolutePath();
                            tvImageSelected.setText("Đã chọn 1 ảnh");
                            ivProductPreview.setImageURI(Uri.fromFile(file));
                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi khi sao chép ảnh!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_edit_product);

        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etFlowerName);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        ivProductPreview = findViewById(R.id.ivProductPreview);
        tvImageSelected = findViewById(R.id.tvImageSelected);

        btnBack.setOnClickListener(v -> finish());

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        if (getIntent() != null && getIntent().hasExtra("FLOWER_ID")) {
            flowerId = getIntent().getIntExtra("FLOWER_ID", -1);
            tvTitle.setText("Cập Nhật Sản Phẩm");
            etName.setText(getIntent().getStringExtra("FLOWER_NAME"));

            double price = getIntent().getDoubleExtra("FLOWER_PRICE", 0);
            etPrice.setText(String.valueOf((long) price));

            selectedImageUriString = getIntent().getStringExtra("FLOWER_IMAGE");
            if (selectedImageUriString != null && !selectedImageUriString.isEmpty()) {
                tvImageSelected.setText("Đã có ảnh cũ");
            }
        } else {
            tvTitle.setText("Thêm Sản Phẩm Mới");
        }

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            priceStr = priceStr.replace(",", "").replace(".", "");
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Định dạng giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu lên hệ thống...");

        Map<String, Object> data = new HashMap<>();
        data.put("flower_name", name);
        data.put("price", price);
        data.put("image_resource", selectedImageUriString);

        SupabaseApi api = SupabaseClient.getApi();

        if (flowerId == -1) {
            data.put("luot_mua", 0);
            data.put("category_id", 1);
            api.addFlower(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminAddEditProductActivity.this, "Đã thêm hoa mới!", Toast.LENGTH_SHORT).show();
                        goToManageProducts();
                    } else resetButton();
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) { resetButton(); }
            });
        } else {
            api.updateFlower("eq." + flowerId, data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminAddEditProductActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        goToManageProducts();
                    } else resetButton();
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) { resetButton(); }
            });
        }
    }

    private void resetButton() {
        btnSave.setEnabled(true);
        btnSave.setText("LƯU SẢN PHẨM");
    }

    private void goToManageProducts() {
        Intent intent = new Intent(this, AdminManageProductsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}