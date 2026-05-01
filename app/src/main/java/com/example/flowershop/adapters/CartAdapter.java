package com.example.flowershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.activities.CartActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartActivity.CartItemWithFlower> items;
    private CartListener listener;

    public interface CartListener {
        void onQuantityChanged(int cartId, int newQuantity);
        void onDelete(int cartId);
    }

    public CartAdapter(List<CartActivity.CartItemWithFlower> items, CartListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartActivity.CartItemWithFlower item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFlower;
        private TextView tvFlowerName, tvPrice, tvQuantity;
        private Button btnDecrease, btnIncrease;
        private ImageButton btnDelete;

        CartViewHolder(View itemView) {
            super(itemView);
            ivFlower = itemView.findViewById(R.id.ivFlower);
            tvFlowerName = itemView.findViewById(R.id.tvFlowerName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(CartActivity.CartItemWithFlower item) {
            tvFlowerName.setText(item.flower.flowerName);
            tvPrice.setText(String.format("%.0f VND", item.flower.price));
            tvQuantity.setText(String.valueOf(item.cart.quantity));

            final int cartId = item.cart.cartID;
            final int currentQty = item.cart.quantity;

            // Decrease button - decrease quantity, or delete if quantity = 1
            btnDecrease.setOnClickListener(v -> {
                int newQty = currentQty - 1;
                if (listener != null) {
                    if (newQty <= 0) {
                        listener.onDelete(cartId);
                    } else {
                        listener.onQuantityChanged(cartId, newQty);
                    }
                }
            });

            // Increase button
            btnIncrease.setOnClickListener(v -> {
                int newQty = currentQty + 1;
                if (listener != null) {
                    listener.onQuantityChanged(cartId, newQty);
                }
            });

            // Delete button - removes item completely
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(cartId);
                }
            });
        }
    }
}