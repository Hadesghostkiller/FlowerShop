package com.example.flowershop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.model.CartItem;
import com.example.flowershop.model.SupabaseFlower;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        SupabaseFlower flower = cartItem.getFlowers();

        if (flower != null) {
            holder.tvName.setText(flower.flowerName != null ? flower.flowerName : "Hoa chưa rõ tên");
            holder.tvPrice.setText(String.format("%,.0f VND", flower.price));

            // TODO: Nếu bạn dùng Glide/Picasso để load ảnh từ link URL, code tại đây:
            // Glide.with(context).load(flower.image_resource).into(holder.ivImage);
        }

        holder.tvQuantity.setText("x" + cartItem.getQuantity());
    }

    // Quan trọng: Hàm này quyết định số lượng item hiển thị
    @Override
    public int getItemCount() {
        if (cartItemList != null) {
            return cartItemList.size();
        }
        return 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartImage);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
        }
    }
}