package com.example.flowershop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.ChatMessageAdapter;
import com.example.flowershop.model.ChatMessage;
import com.example.flowershop.utils.GroqApiService;
import com.example.flowershop.api.SupabaseClient;
import com.example.flowershop.model.SupabaseFlower;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private ImageButton btnBack;
    private TextView tvTitle;
    private ChatMessageAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private GroqApiService groqApiService;
    private String flowerContext = "";
    private boolean isLoadingContext = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        groqApiService = new GroqApiService();

        initViews();
        setupRecyclerView();
        loadFlowerContext();

        addBotMessage("Xin chào! Tôi là trợ lý ảo của FlowerShop. Tôi có thể giúp bạn:\n" +
                "• Tư vấn chọn hoa theo dịp\n" +
                "• Giới thiệu các loại hoa\n" +
                "• Trả lời câu hỏi về shop\n" +
                "• Viết thiệp chúc mừng\n\n" +
                "Bạn cần hỗ trợ gì?");
    }

    private void loadFlowerContext() {
        SupabaseClient.getApi().getFlowers().enqueue(new Callback<List<SupabaseFlower>>() {
            @Override
            public void onResponse(Call<List<SupabaseFlower>> call, Response<List<SupabaseFlower>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (SupabaseFlower flower : response.body()) {
                        sb.append("- ").append(flower.flowerName)
                                .append(", Giá: ").append((int) flower.price).append("đ\n");
                    }
                    flowerContext = sb.toString();
                } else {
                    flowerContext = "Hiện tại chưa có dữ liệu hoa. Vui lòng liên hệ shop để được tư vấn.";
                }
                isLoadingContext = false;
            }

            @Override
            public void onFailure(Call<List<SupabaseFlower>> call, Throwable t) {
                flowerContext = "Hiện tại chưa có dữ liệu hoa. Vui lòng liên hệ shop để được tư vấn.";
                isLoadingContext = false;
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setText("AI Chatbot");
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                processMessage(message);
                etMessage.setText("");
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void processMessage(String userMessage) {
        addUserMessage(userMessage);

        if (isLoadingContext) {
            addBotMessage("Đang tải dữ liệu... Vui lòng chờ một chút.");
            return;
        }

        groqApiService.sendMessage(userMessage, flowerContext, new GroqApiService.Callback() {
            @Override
            public void onSuccess(String response) {
                addBotMessage(response);
            }

            @Override
            public void onError(String error) {
                addBotMessage("Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.");
            }
        });
    }

    private void addUserMessage(String message) {
        messages.add(new ChatMessage(message, true));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String message) {
        messages.add(new ChatMessage(message, false));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }
}