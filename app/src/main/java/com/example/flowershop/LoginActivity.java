package com.example.flowershop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowershop.database.FlowerDatabase;
import com.example.flowershop.database.entity.Account;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvError;
    private FlowerDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database and create default data
        try {
            database = FlowerDatabase.getDatabase(getApplicationContext());
            
            // Create default admin account if not exists
            new Thread(() -> {
                try {
                    List<Account> accounts = database.accountDao().getAllAccountsSync();
                    if (accounts == null || accounts.isEmpty()) {
                        // Create default accounts
                        database.accountDao().insert(new Account("admin", "admin123", "Quan Ly", "admin"));
                        database.accountDao().insert(new Account("user1", "123456", "Khach Hang", "customer"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            Toast.makeText(this, "Loi khoi tao: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> register());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Vui long nhap day du thong tin!");
            return;
        }

        tvError.setText("Dang kiem tra...");
        
        new Thread(() -> {
            try {
                // Wait a bit for data to be created
                Thread.sleep(500);
                
                List<Account> accounts = database.accountDao().getAllAccountsSync();
                
                Account foundAccount = null;
                if (accounts != null) {
                    for (Account a : accounts) {
                        if (a.username.equalsIgnoreCase(username) && a.password.equals(password)) {
                            foundAccount = a;
                            break;
                        }
                    }
                }
                
                final Account finalAccount = foundAccount;
                runOnUiThread(() -> {
                    if (finalAccount != null) {
                        Toast.makeText(LoginActivity.this, "Dang nhap thanh cong!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.putExtra("username", finalAccount.username);
                        intent.putExtra("fullname", finalAccount.fullname);
                        startActivity(intent);
                        finish();
                    } else {
                        tvError.setText("Ten dang nhap hoac mat khau khong dung!");
                    }
                });
            } catch (Exception e) {
                final String error = e.getMessage();
                runOnUiThread(() -> {
                    tvError.setText("Loi: " + error);
                });
            }
        }).start();
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Vui long nhap day du thong tin!");
            return;
        }

        new Thread(() -> {
            try {
                Account existing = database.accountDao().getAccountByUsername(username);
                
                if (existing != null) {
                    runOnUiThread(() -> {
                        tvError.setText("Tai khoan da ton tai!");
                    });
                } else {
                    Account newAccount = new Account(username, password, username, "customer");
                    database.accountDao().insert(newAccount);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Dang ky thanh cong! Vui long dang nhap.", Toast.LENGTH_SHORT).show();
                        etUsername.setText("");
                        etPassword.setText("");
                        tvError.setText("");
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvError.setText("Loi: " + e.getMessage());
                });
            }
        }).start();
    }
}