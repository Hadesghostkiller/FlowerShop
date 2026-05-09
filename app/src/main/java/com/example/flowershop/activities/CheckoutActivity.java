package com.example.flowershop.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etAddress;
    private RadioGroup rgPaymentMethod;
    private Button btnConfirm;
    private ImageButton btnBack;
    private TextView tvTotalAmount;
    private double totalAmount = 0;
    private ArrayList<CartItem> selectedItems = new ArrayList<>();
    private String paymentMethod = "Tiền mặt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initViews();
        loadData();
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

        if (paymentMethod.equals("Tiền mặt")) {
            showCashInputDialog(name, phone, email, address);
        } else {
            showTransferBillPreview(name, phone, email, address);
        }
    }

    private void showCashInputDialog(String name, String phone, String email, String address) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cash_payment, null);
        TextView tvDialogTotal = dialogView.findViewById(R.id.tvDialogTotal);
        EditText etCustomerMoney = dialogView.findViewById(R.id.etCustomerMoney);
        TextView tvChange = dialogView.findViewById(R.id.tvChange);
        Button btnConfirmCash = dialogView.findViewById(R.id.btnConfirmCash);

        tvDialogTotal.setText(String.format("%,.0f VND", totalAmount));

        etCustomerMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                if (input.isEmpty()) {
                    tvChange.setText("0 VND");
                    btnConfirmCash.setEnabled(false);
                    return;
                }
                try {
                    double customerMoney = Double.parseDouble(input);
                    double change = customerMoney - totalAmount;
                    if (customerMoney > 0 && change >= 0) {
                        tvChange.setText(String.format("%,.0f VND", change));
                        btnConfirmCash.setEnabled(true);
                    } else {
                        tvChange.setText("Không đủ tiền!");
                        btnConfirmCash.setEnabled(false);
                    }
                } catch (NumberFormatException e) {
                    tvChange.setText("0 VND");
                    btnConfirmCash.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        AlertDialog cashDialog = new AlertDialog.Builder(this)
                .setTitle("Thanh toán tiền mặt")
                .setView(dialogView)
                .setCancelable(false)
                .show();

        btnConfirmCash.setOnClickListener(v -> {
            double customerMoney = Double.parseDouble(etCustomerMoney.getText().toString().trim());
            double change = customerMoney - totalAmount;
            cashDialog.dismiss();
            showCashBillPreview(name, phone, email, address, customerMoney, change);
        });
    }

    private void showCashBillPreview(String name, String phone, String email, String address,
                                     double customerMoney, double change) {
        View billView = getLayoutInflater().inflate(R.layout.dialog_bill_preview, null);
        TextView tvContent = billView.findViewById(R.id.tvBillContent);
        ImageView ivQr = billView.findViewById(R.id.ivQrCode);
        ivQr.setVisibility(View.GONE);
        tvContent.setText(buildBillContent(name, phone, email, address, customerMoney, change));

        new AlertDialog.Builder(this)
                .setTitle("Hóa đơn")
                .setView(billView)
                .setPositiveButton("Đóng", (d, w) -> saveOrdersSeparately())
                .setCancelable(false)
                .show();
    }

    private void showTransferBillPreview(String name, String phone, String email, String address) {
        View billView = getLayoutInflater().inflate(R.layout.dialog_bill_preview, null);
        TextView tvContent = billView.findViewById(R.id.tvBillContent);
        ImageView ivQr = billView.findViewById(R.id.ivQrCode);
        ivQr.setVisibility(View.VISIBLE);

        String qrUrl = buildVietQrUrl(totalAmount);
        Glide.with(this)
                .load(qrUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.qr_code)
                .into(ivQr);

        tvContent.setText(buildTransferBillContent(name, phone, email, address));

        new AlertDialog.Builder(this)
                .setTitle("Hóa đơn")
                .setView(billView)
                .setPositiveButton("Đóng", (d, w) -> saveOrdersSeparately())
                .setCancelable(false)
                .show();
    }

    private void saveOrdersSeparately() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null || selectedItems.isEmpty()) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String customerName = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        final int totalRequests = selectedItems.size();
        final int[] completedRequests = {0};

        for (CartItem item : selectedItems) {
            Order order = new Order();
            order.user_id = userId;
            order.customer_name = customerName;
            order.phone = phone;
            order.address = address;
            order.payment_method = paymentMethod;
            order.status = "Hoàn tất";

            // Lưu thông tin chi tiết của DUY NHẤT sản phẩm này
            String flowerName = (item.getFlowers() != null) ? item.getFlowers().flowerName : "Hoa";
            order.order_details = flowerName + " (x" + item.getQuantity() + ")";

            // Tính số tiền riêng cho sản phẩm này (Số lượng x Đơn giá)
            double itemPrice = (item.getFlowers() != null) ? item.getFlowers().price : 0;
            order.total_amount = item.getQuantity() * itemPrice;

            SupabaseClient.getApi().createOrder("return=representation", order).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    completedRequests[0]++;
                    checkAndFinish(completedRequests[0], totalRequests);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    completedRequests[0]++;
                    checkAndFinish(completedRequests[0], totalRequests);
                }
            });
        }
    }

    private void checkAndFinish(int current, int total) {
        if (current == total) {
            Log.d("CHECKOUT", "Tất cả các món đã được lưu riêng biệt.");
            clearCartAndReset();
        }
    }

    private String buildVietQrUrl(double amount) {
        String bankCode = "techcombank";
        String accountNo = "792401048888";
        String template = "compact";
        String info = "Thanh toan FlowerShop";
        try {
            info = java.net.URLEncoder.encode(info, "UTF-8");
        } catch (Exception ignored) {}
        return String.format(Locale.US,
                "https://img.vietqr.io/image/%s-%s-%s.png?amount=%.0f&addInfo=%s",
                bankCode, accountNo, template, amount, info);
    }

    private String buildBillContent(String name, String phone, String email, String address,
                                    double customerMoney, double change) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date());

        StringBuilder bill = new StringBuilder();
        bill.append("          HÓA ĐƠN THANH TOÁN\n\n");
        bill.append("Ngày: ").append(date).append("\n");
        bill.append("Khách hàng: ").append(name).append("\n");
        bill.append("SĐT: ").append(phone).append("\n");
        bill.append("Email: ").append(email).append("\n");
        bill.append("Địa chỉ: ").append(address).append("\n\n");
        bill.append("Phương thức: ").append(paymentMethod).append("\n\n");
        bill.append("--- Sản phẩm đã mua ---\n");

        int stt = 1;
        for (CartItem item : selectedItems) {
            String itemName = item.getFlowers() != null ? item.getFlowers().flowerName : "Sản phẩm";
            double itemPrice = item.getFlowers() != null ? item.getFlowers().price : 0;
            bill.append(stt++).append(". ").append(itemName).append("\n");
            bill.append("   SL: ").append(item.getQuantity());
            bill.append(" x ").append(String.format("%,.0fđ", itemPrice)).append("\n");
        }

        bill.append("\n");
        bill.append("Tiền khách đưa: ").append(String.format("%,.0f VND", customerMoney)).append("\n");
        bill.append("Tiền trả khách: ").append(String.format("%,.0f VND", change)).append("\n");
        bill.append("TỔNG CỘNG: ").append(String.format("%,.0f VND", totalAmount)).append("\n\n");
        bill.append("  Cảm ơn quý khách!\n");
        bill.append("  Chúc một ngày đẹp trời!");

        return bill.toString();
    }

    private String buildTransferBillContent(String name, String phone, String email, String address) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date());

        StringBuilder bill = new StringBuilder();
        bill.append("          HÓA ĐƠN THANH TOÁN\n\n");
        bill.append("Ngày: ").append(date).append("\n");
        bill.append("Khách hàng: ").append(name).append("\n");
        bill.append("SĐT: ").append(phone).append("\n");
        bill.append("Email: ").append(email).append("\n");
        bill.append("Địa chỉ: ").append(address).append("\n\n");
        bill.append("Phương thức: ").append(paymentMethod).append("\n\n");
        bill.append("--- Sản phẩm đã mua ---\n");

        int stt = 1;
        for (CartItem item : selectedItems) {
            String itemName = item.getFlowers() != null ? item.getFlowers().flowerName : "Sản phẩm";
            double itemPrice = item.getFlowers() != null ? item.getFlowers().price : 0;
            bill.append(stt++).append(". ").append(itemName).append("\n");
            bill.append("   SL: ").append(item.getQuantity());
            bill.append(" x ").append(String.format("%,.0fđ", itemPrice)).append("\n");
        }

        bill.append("\n");
        bill.append("Tiền khách chuyển: ").append(String.format("%,.0f VND", totalAmount)).append("\n\n");
        bill.append("Thông tin chuyển khoản:\n");
        bill.append("Ngân hàng: Techcombank\n");
        bill.append("STK: 792401048888\n");
        bill.append("Chủ TK: FlowerShop\n\n");
        bill.append("  Cảm ơn quý khách!\n");
        bill.append("  Chúc một ngày đẹp trời!");

        return bill.toString();
    }

    private void clearCartAndReset() {
        for (CartItem item : selectedItems) {
            String qUserId = "eq." + item.getUser_id();
            String qFlowerId = "eq." + item.getFlower_id();

            SupabaseClient.getApi().deleteCartItem(qUserId, qFlowerId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {}
                @Override
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        }

        selectedItems.clear();
        totalAmount = 0;
        tvTotalAmount.setText("0 VND");
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đã thanh toán");
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}