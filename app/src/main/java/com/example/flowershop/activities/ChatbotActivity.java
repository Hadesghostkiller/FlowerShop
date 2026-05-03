package com.example.flowershop.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.ChatMessageAdapter;
import com.example.flowershop.database.FlowerDatabase;
import com.example.flowershop.database.entity.ChatbotResponse;
import com.example.flowershop.database.entity.Flower;
import com.example.flowershop.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatMessageAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private FlowerDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        database = FlowerDatabase.getDatabase(getApplicationContext());
        initViews();
        setupRecyclerView();
        setupClickListeners();

        // Welcome message
        addBotMessage("Xin chào! Tôi là trợ lý ảo FlowerShop. Tôi có thể giúp bạn:\n1. Tư vấn chọn hoa theo dịp\n2. Viết thiệp tự động\n3. Tra cứu thông tin hoa\nBạn cần hỗ trợ gì?");
    }

    private void initViews() {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnConsult).setOnClickListener(v -> showConsultDialog());
        findViewById(R.id.btnCard).setOnClickListener(v -> showCardDialog());
        findViewById(R.id.btnRecognize).setOnClickListener(v -> showRecognizeDialog());
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        addUserMessage(text);
        etMessage.setText("");

        // Process message
        processUserMessage(text.toLowerCase());
    }

    private void processUserMessage(String message) {
        // Check for flower keywords
        if (message.contains("sinh nhật") || message.contains("sinh nhat") || message.contains("birthday")) {
            respondWithFlowers("Sinh Nhat");
        } else if (message.contains("khai trương") || message.contains("khai truong")) {
            respondWithFlowers("Khai Truong");
        } else if (message.contains("chia buồn") || message.contains("chia buon") || message.contains("tang")) {
            respondWithFlowers("Chia Buon");
        } else if (message.contains("cưới") || message.contains("cuoi") || message.contains("wedding")) {
            respondWithFlowers("Hoa Cuoi");
        } else if (message.contains("hoa") || message.contains("flower")) {
            respondWithFlowers("Hoa Bo");
        } else {
            // Search in ChatbotResponse table
            new Thread(() -> {
                List<ChatbotResponse> responses = database.chatbotResponseDao().searchByKeyword(message);
                runOnUiThread(() -> {
                    if (!responses.isEmpty()) {
                        addBotMessage(responses.get(0).response);
                    } else {
                        addBotMessage("Xin lỗi, tôi chưa hiểu ý bạn. Bạn có thể hỏi về:\n- Tư vấn hoa theo dịp (sinh nhật, khai trương...)\n- Viết thiệp tự động\n- Tra cứu giá hoa");
                    }
                });
            }).start();
        }
    }

    private void respondWithFlowers(String category) {
        new Thread(() -> {
            List<Flower> flowers = database.flowerDao().getFlowersByCategorySync(category);
            StringBuilder sb = new StringBuilder();
            sb.append("Gợi ý hoa loại \"").append(category).append("\":\n");
            for (int i = 0; i < flowers.size(); i++) {
                Flower f = flowers.get(i);
                sb.append(i + 1).append(". ").append(f.flowerName)
                        .append(" (").append(String.format("%.0f", f.price)).append(" VND)\n");
            }
            runOnUiThread(() -> addBotMessage(sb.toString()));
        }).start();
    }

    private void showConsultDialog() {
        String[] categories = {"Sinh Nhật", "Khai Trương", "Chia Buồn", "Hoa Cưới", "Hoa Bó"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn dịp")
                .setItems(categories, (dialog, which) -> {
                    String cat = "";
                    switch (which) {
                        case 0: cat = "Sinh Nhat"; break;
                        case 1: cat = "Khai Truong"; break;
                        case 2: cat = "Chia Buon"; break;
                        case 3: cat = "Hoa Cuoi"; break;
                        case 4: cat = "Hoa Bo"; break;
                    }
                    addUserMessage("Tư vấn hoa " + categories[which]);
                    respondWithFlowers(cat);
                })
                .show();
    }

    private void showCardDialog() {
        String[] occasions = {"Sinh Nhật", "Khai Trương", "Chia Buồn", "Hoa Cưới"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn dịp viết thiệp")
                .setItems(occasions, (dialog, which) -> {
                    String occasion = "";
                    switch (which) {
                        case 0: occasion = "Sinh Nhat"; break;
                        case 1: occasion = "Khai Truong"; break;
                        case 2: occasion = "Chia Buon"; break;
                        case 3: occasion = "Hoa Cuoi"; break;
                    }
                    showCardForm(occasion, occasions[which]);
                })
                .show();
    }

    private void showCardForm(String occasion, String displayName) {
        View view = getLayoutInflater().inflate(R.layout.dialog_card_form, null);
        EditText etTen = view.findViewById(R.id.etTen);
        EditText etMessage = view.findViewById(R.id.etMessage);
        EditText etNguoiGui = view.findViewById(R.id.etNguoiGui);

        new AlertDialog.Builder(this)
                .setTitle("Viết thiệp: " + displayName)
                .setView(view)
                .setPositiveButton("Tạo thiệp", (dialog, which) -> {
                    String ten = etTen.getText().toString();
                    String msg = etMessage.getText().toString();
                    String nguoiGui = etNguoiGui.getText().toString();

                    new Thread(() -> {
                        com.example.flowershop.database.entity.CardTemplate template =
                                database.cardTemplateDao().getByOccasion(occasion);
                        runOnUiThread(() -> {
                            if (template != null) {
                                String card = template.template
                                        .replace("{ten}", ten)
                                        .replace("{message}", msg)
                                        .replace("{nguoi_gui}", nguoiGui);
                                addUserMessage("Viết thiệp " + displayName);
                                addBotMessage("Thiệp của bạn:\n" + card);
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRecognizeDialog() {
        String[] options = {"Chụp ảnh", "Chọn từ thư viện", "Tự chọn loại hoa"};
        new AlertDialog.Builder(this)
                .setTitle("Nhận diện hoa")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                        case 1:
                            addUserMessage("Nhận diện hoa qua ảnh");
                            addBotMessage("Tính năng nhận diện ảnh đang được phát triển. Bạn có thể tự chọn loại hoa bên dưới.");
                            showFlowerCategoryDialog();
                            break;
                        case 2:
                            showFlowerCategoryDialog();
                            break;
                    }
                })
                .show();
    }

    private void showFlowerCategoryDialog() {
        String[] categories = {"Hoa Bó", "Sinh Nhật", "Khai Trương", "Chia Buồn", "Hoa Cưới"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn loại hoa")
                .setItems(categories, (dialog, which) -> {
                    String cat = "";
                    switch (which) {
                        case 0: cat = "Hoa Bo"; break;
                        case 1: cat = "Sinh Nhat"; break;
                        case 2: cat = "Khai Truong"; break;
                        case 3: cat = "Chia Buon"; break;
                        case 4: cat = "Hoa Cuoi"; break;
                    }
                    addUserMessage("Hoa loại: " + categories[which]);
                    respondWithFlowers(cat);
                })
                .show();
    }

    private void addUserMessage(String message) {
        messages.add(new ChatMessage(message, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String message) {
        messages.add(new ChatMessage(message, ChatMessage.TYPE_BOT));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }
}
