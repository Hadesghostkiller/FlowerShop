package com.example.flowershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.model.ChatMessage;
import com.example.flowershop.utils.TypewriterTextView;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private OnMessageAnimatedListener animationListener;

    public interface OnMessageAnimatedListener {
        void onAnimationComplete(int position);
    }

    public void setOnMessageAnimatedListener(OnMessageAnimatedListener listener) {
        this.animationListener = listener;
    }

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.bind(msg, position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TypewriterTextView tvMessage;

        MessageViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }

        void bind(ChatMessage msg, int position) {
            if (msg.isUser()) {
                // User message - show immediately
                tvMessage.setTypewriterText(msg.getMessage(), 0);
                tvMessage.setBackgroundResource(R.drawable.bg_chat_user);
                tvMessage.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
                
                // Align to right
                ((FrameLayout.LayoutParams) tvMessage.getLayoutParams()).gravity = android.view.Gravity.END;
            } else {
                // Bot message - typewriter effect
                tvMessage.setBackgroundResource(R.drawable.bg_chat_bot);
                tvMessage.setTextColor(itemView.getContext().getResources().getColor(R.color.black, null));
                
                // Align to left
                ((FrameLayout.LayoutParams) tvMessage.getLayoutParams()).gravity = android.view.Gravity.START;
                
                // Typewriter animation with callback
                tvMessage.setTypewriterText(msg.getMessage(), 25);
            }
        }
    }
}