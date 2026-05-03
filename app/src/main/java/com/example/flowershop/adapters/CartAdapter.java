package com.example.flowershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public CartAdapter() {
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlowerName, tvPrice, tvQuantity;

        CartViewHolder(View itemView) {
            super(itemView);
            tvFlowerName = itemView.findViewById(R.id.tvFlowerName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}