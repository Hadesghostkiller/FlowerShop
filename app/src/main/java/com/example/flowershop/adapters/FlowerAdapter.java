package com.example.flowershop.adapters;

import android.content.Context;
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
import com.example.flowershop.model.SupabaseFlower;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder> {

    private List<SupabaseFlower> flowers = new ArrayList<>();
    private OnAddToCartListener listener;

    public interface OnAddToCartListener {
        void onAddToCart(SupabaseFlower flower);
    }

    public FlowerAdapter(OnAddToCartListener listener) {
        this.listener = listener;
    }

    public void setFlowersFromSupabase(List<SupabaseFlower> flowers) {
        this.flowers = flowers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flower, parent, false);
        return new FlowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerViewHolder holder, int position) {
        SupabaseFlower flower = flowers.get(position);
        holder.bind(flower);
    }

    @Override
    public int getItemCount() {
        return flowers.size();
    }

    class FlowerViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFlower;
        private TextView tvFlowerName, tvCategory, tvPrice;
        private Button btnAddToCart;

        FlowerViewHolder(View itemView) {
            super(itemView);
            ivFlower = itemView.findViewById(R.id.ivFlower);
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

            try {
                String path = "flower_image/" + flower.imageResource + ".png";
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

            btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCart(flower);
                }
            });
        }
    }
}
