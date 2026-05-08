package com.example.flowershop.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.activities.ProductDetailActivity;
import com.example.flowershop.model.SupabaseFlower;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder> {

    private List<SupabaseFlower> flowers = new ArrayList<>();
    private Set<Integer> favoriteFlowerIds = new HashSet<>();
    private OnAddToCartListener listener;
    private int layoutResId = R.layout.item_flower;

    public interface OnAddToCartListener {
        void onAddToCart(SupabaseFlower flower);
    }

    public FlowerAdapter(OnAddToCartListener listener) {
        this.listener = listener;
    }

    public FlowerAdapter(OnAddToCartListener listener, int layoutResId) {
        this.listener = listener;
        this.layoutResId = layoutResId;
    }

    public void setFlowersFromSupabase(List<SupabaseFlower> flowers) {
        this.flowers = flowers;
        notifyDataSetChanged();
    }

    public void setFavoriteFlowerIds(List<Integer> ids) {
        this.favoriteFlowerIds = new HashSet<>(ids);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, parent, false);
        return new FlowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerViewHolder holder, int position) {
        SupabaseFlower flower = flowers.get(position);
        holder.bind(flower);
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("flower", flower);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return flowers.size();
    }

    class FlowerViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFlower, ivFavorite;
        private TextView tvFlowerName, tvCategory, tvPrice;
        private Button btnAddToCart;

        FlowerViewHolder(View itemView) {
            super(itemView);
            ivFlower = itemView.findViewById(R.id.ivFlower);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvFlowerName = itemView.findViewById(R.id.tvFlowerName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        void bind(SupabaseFlower flower) {
            Context context = itemView.getContext();
            tvFlowerName.setText(flower.flowerName);
            tvCategory.setText(flower.category);
            tvPrice.setText(String.format("%.0f VND", flower.price));

            if (ivFavorite != null) {
                if (favoriteFlowerIds.contains(flower.id)) {
                    ivFavorite.setVisibility(View.VISIBLE);
                } else {
                    ivFavorite.setVisibility(View.GONE);
                }
            }

            if (ivFlower != null) {
                try {
                    String imageName = (flower.imageResource != null) ? flower.imageResource.trim() : "default";
                    if (!imageName.toLowerCase().endsWith(".png")) {
                        imageName += ".png";
                    }
                    String path = "flower_image/" + imageName;
                    InputStream is = context.getAssets().open(path);
                    Drawable d = Drawable.createFromStream(is, null);
                    ivFlower.setImageDrawable(d);
                    is.close();
                } catch (Exception e) {
                    try {
                        InputStream isDefault = context.getAssets().open("flower_image/default.png");
                        Drawable dDefault = Drawable.createFromStream(isDefault, null);
                        ivFlower.setImageDrawable(dDefault);
                        isDefault.close();
                    } catch (Exception ex) {
                        ivFlower.setImageResource(android.R.drawable.ic_menu_report_image);
                    }
                }
            }

            if (btnAddToCart != null) {
                btnAddToCart.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAddToCart(flower);
                    }
                });
            }
        }
    }
}
