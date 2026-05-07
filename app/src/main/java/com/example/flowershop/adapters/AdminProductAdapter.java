package com.example.flowershop.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flowershop.R;
import com.example.flowershop.model.SupabaseFlower;

import java.io.InputStream;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminViewHolder> {

    private Context context;
    private List<SupabaseFlower> flowerList;
    private OnAdminProductListener listener;

    public interface OnAdminProductListener {
        void onEditClick(SupabaseFlower flower);
        void onDeleteClick(SupabaseFlower flower);
    }

    public AdminProductAdapter(Context context, List<SupabaseFlower> flowerList, OnAdminProductListener listener) {
        this.context = context;
        this.flowerList = flowerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        SupabaseFlower flower = flowerList.get(position);

        holder.tvName.setText(flower.flowerName);
        holder.tvPrice.setText(String.format("Giá: %,.0f VND", flower.price));

        if (flower.imageResource != null && !flower.imageResource.isEmpty()) {
            // ĐÃ SỬA: Nhận diện đường dẫn nội bộ (chứa dấu "/")
            if (flower.imageResource.contains("/") || flower.imageResource.startsWith("content://")) {
                Glide.with(context)
                        .load(flower.imageResource)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivImage);
            } else {
                try {
                    String imagePath = "flower_image/" + flower.imageResource + ".png";
                    InputStream is = context.getAssets().open(imagePath);
                    Drawable d = Drawable.createFromStream(is, null);
                    holder.ivImage.setImageDrawable(d);
                } catch (Exception e) {
                    holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(flower));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(flower));
    }

    @Override
    public int getItemCount() {
        return flowerList != null ? flowerList.size() : 0;
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice;
        ImageButton btnEdit, btnDelete;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivAdminImage);
            tvName = itemView.findViewById(R.id.tvAdminName);
            tvPrice = itemView.findViewById(R.id.tvAdminPrice);
            btnEdit = itemView.findViewById(R.id.btnAdminEdit);
            btnDelete = itemView.findViewById(R.id.btnAdminDelete);
        }
    }
}