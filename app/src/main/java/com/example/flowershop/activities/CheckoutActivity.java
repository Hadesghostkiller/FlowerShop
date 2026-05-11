package com.example.flowershop.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.flowershop.R;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.CartItem;
import com.example.flowershop.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etAddress;
    private RadioGroup rgPaymentMethod;
    private Button btnConfirm;
    private ImageButton btnBack;
    private TextView tvTotalAmount;
    private LinearLayout layoutOrderItems;
    
    private double totalAmount = 0;
    private ArrayList<CartItem> selectedItems = new ArrayList<>();
    private String paymentMethod = "Tiền mặt";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String appId = "default-app-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        initViews();
        loadData();
        loadUserData();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        layoutOrderItems = findViewById(R.id.layoutOrderItems);

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCash) {
                paymentMethod = "Tiền mặt";
            } else if (checkedId == R.id.rbTransfer) {
                paymentMethod = "Chuyển khoản";
            }
        });

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> validateAndProceed());
    }

    private void loadData() {
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");
        if (selectedItems == null) selectedItems = new ArrayList<>();
        
        tvTotalAmount.setText(String.format("%,.0f VND", totalAmount));
        
        layoutOrderItems.removeAllViews();
        int stt = 1;
        for (CartItem item : selectedItems) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_checkout_product, layoutOrderItems, false);
            
            TextView tvProductName = itemView.findViewById(R.id.tvProductName);
            TextView tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            Button btnEditDate = itemView.findViewById(R.id.btnEditDate);
            Button btnEditTime = itemView.findViewById(R.id.btnEditTime);
            EditText etEditCardMess = itemView.findViewById(R.id.etEditCardMess);
            EditText etEditInstructions = itemView.findViewById(R.id.etEditInstructions);

            String name = item.getFlowers() != null ? item.getFlowers().flowerName : "Hoa";
            tvProductName.setText(stt++ + ". " + name + " (Mã: " + item.getMa_don_hang() + ")");
            tvProductQuantity.setText("Số lượng: " + item.getQuantity());

            // Initialize display for Date/Time
            updateDateTimeButtons(item, btnEditDate, btnEditTime);
            etEditCardMess.setText(item.getCard_mess());
            etEditInstructions.setText(item.getInstructions());

            // Listeners for Editing (Changes only in local selectedItems list)
            btnEditDate.setOnClickListener(v -> showDatePicker(item, btnEditDate, btnEditTime));
            btnEditTime.setOnClickListener(v -> showTimePicker(item, btnEditDate, btnEditTime));

            etEditCardMess.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setCard_mess(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });

            etEditInstructions.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setInstructions(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });

            layoutOrderItems.addView(itemView);
        }
    }

    private void updateDateTimeButtons(CartItem item, Button btnDate, Button btnTime) {
        String dt = item.getDate_time();
        if (dt != null && !dt.isEmpty()) {
            String[] parts = dt.split(" ");
            if (parts.length >= 2) {
                btnDate.setText(parts[0]);
                btnTime.setText(parts[1]);
            } else {
                btnDate.setText(dt);
            }
        }
    }

    private void showDatePicker(CartItem item, Button btnDate, Button btnTime) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            btnDate.setText(date);
            combineDateTime(item, btnDate, btnTime);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(CartItem item, Button btnDate, Button btnTime) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            btnTime.setText(time);
            combineDateTime(item, btnDate, btnTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void combineDateTime(CartItem item, Button btnDate, Button btnTime) {
        String d = btnDate.getText().toString();
        String t = btnTime.getText().toString();
        if (!d.equals("Chọn Ngày") && !t.equals("Chọn Giờ")) {
            item.setDate_time(d + " " + t);
        } else if (!d.equals("Chọn Ngày")) {
            item.setDate_time(d);
        }
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        etEmail.setText(user.getEmail());
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) etName.setText(user.getDisplayName());
        db.collection("artifacts").document(appId).collection("users").document(user.getUid()).collection("profile").document("data").get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String name = doc.getString("name");
                String phone = doc.getString("phone");
                if (name != null && !name.trim().isEmpty()) etName.setText(name);
                if (phone != null && !phone.trim().isEmpty()) etPhone.setText(phone);
            }
        });
    }

    private void validateAndProceed() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty()) { etName.setError("Vui lòng nhập họ tên"); return; }
        if (phone.isEmpty()) { etPhone.setError("Vui lòng nhập số điện thoại"); return; }
        if (email.isEmpty()) { etEmail.setError("Vui lòng nhập email"); return; }
        if (address.isEmpty()) { etAddress.setError("Vui lòng nhập địa chỉ"); return; }

        // Kiểm tra bắt buộc phải có ngày giờ cho từng sản phẩm
        for (CartItem item : selectedItems) {
            if (item.getDate_time() == null || item.getDate_time().isEmpty() || item.getDate_time().length() < 11) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày và giờ cho tất cả sản phẩm!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (paymentMethod.equals("Tiền mặt")) showCashInputDialog(name, phone, email, address);
        else showTransferBillPreview(name, phone, email, address);
    }

    private void showCashInputDialog(String name, String phone, String email, String address) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cash_payment, null);
        TextView tvDialogTotal = dialogView.findViewById(R.id.tvDialogTotal);
        EditText etCustomerMoney = dialogView.findViewById(R.id.etCustomerMoney);
        TextView tvChange = dialogView.findViewById(R.id.tvChange);
        Button btnConfirmCash = dialogView.findViewById(R.id.btnConfirmCash);

        tvDialogTotal.setText(String.format("%,.0f VND", totalAmount));
        etCustomerMoney.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double customerMoney = Double.parseDouble(s.toString());
                    double change = customerMoney - totalAmount;
                    if (change >= 0) {
                        tvChange.setText(String.format("%,.0f VND", change));
                        btnConfirmCash.setEnabled(true);
                    } else {
                        tvChange.setText("Không đủ tiền!");
                        btnConfirmCash.setEnabled(false);
                    }
                } catch (Exception e) {
                    tvChange.setText("0 VND");
                    btnConfirmCash.setEnabled(false);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        AlertDialog cashDialog = new AlertDialog.Builder(this).setTitle("Thanh toán tiền mặt").setView(dialogView).setCancelable(false).show();
        btnConfirmCash.setOnClickListener(v -> {
            double m = Double.parseDouble(etCustomerMoney.getText().toString());
            cashDialog.dismiss();
            showCashBillPreview(name, phone, email, address, m, m - totalAmount);
        });
    }

    private void showCashBillPreview(String name, String phone, String email, String address, double m, double c) {
        View v = getLayoutInflater().inflate(R.layout.dialog_bill_preview, null);
        ((TextView) v.findViewById(R.id.tvBillContent)).setText(buildBillContent(name, phone, email, address, m, c));
        v.findViewById(R.id.ivQrCode).setVisibility(View.GONE);
        new AlertDialog.Builder(this).setTitle("Hóa đơn").setView(v).setPositiveButton("Đóng", (d, w) -> saveOrdersSeparately()).setCancelable(false).show();
    }

    private void showTransferBillPreview(String name, String phone, String email, String address) {
        View v = getLayoutInflater().inflate(R.layout.dialog_bill_preview, null);
        ImageView iv = v.findViewById(R.id.ivQrCode);
        iv.setVisibility(View.VISIBLE);
        Glide.with(this).load(buildVietQrUrl(totalAmount)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(iv);
        ((TextView) v.findViewById(R.id.tvBillContent)).setText(buildTransferBillContent(name, phone, email, address));
        new AlertDialog.Builder(this).setTitle("Hóa đơn").setView(v).setPositiveButton("Đóng", (d, w) -> saveOrdersSeparately()).setCancelable(false).show();
    }

    private void saveOrdersSeparately() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        final int total = selectedItems.size();
        final int[] count = {0};
        for (CartItem item : selectedItems) {
            Order o = new Order();
            o.user_id = user.getUid();
            o.customer_name = etName.getText().toString().trim();
            o.phone = etPhone.getText().toString().trim();
            o.address = etAddress.getText().toString().trim();
            o.payment_method = paymentMethod;
            o.status = "Hoàn tất";
            o.order_details = (item.getFlowers() != null ? item.getFlowers().flowerName : "Hoa") + " (Mã: " + item.getMa_don_hang() + ") x" + item.getQuantity();
            o.total_amount = item.getQuantity() * (item.getFlowers() != null ? item.getFlowers().price : 0);

            SupabaseClient.getApi().createOrder("return=representation", o).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                    updateFlowerStock(item);
                    count[0]++;
                    if (count[0] == total) clearCartAndReset();
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {
                    count[0]++;
                    if (count[0] == total) clearCartAndReset();
                }
            });
        }
    }

    private void updateFlowerStock(CartItem item) {
        if (item.getFlowers() == null) return;
        Map<String, Object> u = new HashMap<>();
        u.put("stock", Math.max(0, item.getFlowers().stock - item.getQuantity()));
        u.put("luot_mua", item.getFlowers().luotMua + item.getQuantity());
        SupabaseClient.getApi().updateFlower("eq." + item.getFlower_id(), u).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private String buildVietQrUrl(double a) {
        return String.format(Locale.US, "https://img.vietqr.io/image/techcombank-792401048888-compact.png?amount=%.0f&addInfo=Thanh%%20toan%%20FlowerShop", a);
    }

    private String buildBillContent(String n, String p, String e, String a, double m, double c) {
        StringBuilder b = new StringBuilder("          HÓA ĐƠN THANH TOÁN\n\n");
        b.append("Ngày: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date())).append("\n");
        b.append("Khách: ").append(n).append("\nSĐT: ").append(p).append("\nEmail: ").append(e).append("\nĐịa chỉ: ").append(a).append("\n\n");
        b.append("--- Chi tiết đơn hàng ---\n");
        int stt = 1;
        for (CartItem item : selectedItems) {
            b.append("┌───────────────────────────┐\n");
            b.append("  ").append(stt++).append(". ").append(item.getFlowers() != null ? item.getFlowers().flowerName : "Hoa").append("\n");
            b.append("  Mã hàng: ").append(item.getMa_don_hang()).append("\n");
            b.append("  Số lượng: ").append(item.getQuantity()).append("\n");
            b.append("  Hẹn nhận: ").append(item.getDate_time()).append("\n");
            if (item.getCard_mess() != null && !item.getCard_mess().isEmpty()) b.append("  Lời chúc: ").append(item.getCard_mess()).append("\n");
            if (item.getInstructions() != null && !item.getInstructions().isEmpty()) b.append("  Ghi chú: ").append(item.getInstructions()).append("\n");
            b.append("└───────────────────────────┘\n");
        }
        b.append("\nTiền khách đưa: ").append(String.format("%,.0f VND", m));
        b.append("\nTiền trả khách: ").append(String.format("%,.0f VND", c));
        b.append("\nTỔNG CỘNG: ").append(String.format("%,.0f VND", totalAmount));
        return b.toString();
    }

    private String buildTransferBillContent(String n, String p, String e, String a) {
        StringBuilder b = new StringBuilder("          HÓA ĐƠN THANH TOÁN\n\n");
        b.append("Ngày: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date())).append("\n");
        b.append("Khách: ").append(n).append("\nSĐT: ").append(p).append("\nEmail: ").append(e).append("\nĐịa chỉ: ").append(a).append("\n\n");
        b.append("--- Chi tiết đơn hàng ---\n");
        int stt = 1;
        for (CartItem item : selectedItems) {
            b.append("┌───────────────────────────┐\n");
            b.append("  ").append(stt++).append(". ").append(item.getFlowers() != null ? item.getFlowers().flowerName : "Hoa").append("\n");
            b.append("  Mã hàng: ").append(item.getMa_don_hang()).append("\n");
            b.append("  Số lượng: ").append(item.getQuantity()).append("\n");
            b.append("  Hẹn nhận: ").append(item.getDate_time()).append("\n");
            if (item.getCard_mess() != null && !item.getCard_mess().isEmpty()) b.append("  Lời chúc: ").append(item.getCard_mess()).append("\n");
            if (item.getInstructions() != null && !item.getInstructions().isEmpty()) b.append("  Ghi chú: ").append(item.getInstructions()).append("\n");
            b.append("└───────────────────────────┘\n");
        }
        b.append("\nTỔNG CỘNG: ").append(String.format("%,.0f VND", totalAmount));
        return b.toString();
    }

    private void clearCartAndReset() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        final int total = selectedItems.size();
        final int[] deleted = {0};
        for (CartItem item : selectedItems) {
            SupabaseClient.getApi().deleteCartItem("eq." + user.getUid(), "eq." + item.getFlower_id()).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                    deleted[0]++;
                    if (deleted[0] == total) {
                        Toast.makeText(CheckoutActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {
                    deleted[0]++;
                    if (deleted[0] == total) {
                        Toast.makeText(CheckoutActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }
}