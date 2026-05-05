package com.example.flowershop.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etPhone, etDob;
    private Button btnSave;
    private ImageView btnBack;
    private View loadingOverlay;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private final String appId = "default-app-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            return;
        }
        userId = user.getUid();

        initViews();
        loadCurrentData();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void loadCurrentData() {
        String currentName = getIntent().getStringExtra("name");
        String currentPhone = getIntent().getStringExtra("phone");
        String currentDob = getIntent().getStringExtra("dob");

        if (currentName != null) etName.setText(currentName);

        // Xử lý Số điện thoại
        if (currentPhone != null && !currentPhone.equalsIgnoreCase("Cập nhật") && !currentPhone.toLowerCase().contains("chưa")) {
            etPhone.setText(currentPhone);
        } else {
            etPhone.setText(""); // Để trống để hiện Hint
        }

        // Xử lý Ngày sinh: Kiểm tra xem có chứa dd/mm/yyyy hoặc Cập nhật không
        if (currentDob != null && currentDob.contains(": ")) {
            String dobValue = currentDob.split(": ")[1];
            if (!dobValue.equalsIgnoreCase("dd/mm/yyyy") && !dobValue.equalsIgnoreCase("Cập nhật") && !dobValue.toLowerCase().contains("chưa")) {
                etDob.setText(dobValue);
            } else {
                etDob.setText(""); // Để trống để hiện Hint "Chọn ngày sinh"
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etDob.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
                etDob.setText(day + "/" + (month + 1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnSave.setOnClickListener(v -> saveToFirestore());
    }

    private void saveToFirestore() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        map.put("dob", dob);

        db.collection("artifacts").document(appId)
                .collection("users").document(userId)
                .collection("profile").document("data")
                .set(map, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}