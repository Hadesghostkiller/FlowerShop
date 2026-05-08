package com.example.flowershop.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.model.Category;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_style, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }

        void bind(Category category) {
            tvCategoryName.setText(category.name);
            Context context = itemView.getContext();
            
            // Map category name to image asset
            String imageName = "";
            String categoryName = category.name.toLowerCase();
            if (categoryName.contains("tình yêu")) {
                imageName = "tinhyeu.png";
            } else if (categoryName.contains("chúc mừng")) {
                imageName = "chucmung.png";
            } else if (categoryName.contains("chia buồn")) {
                imageName = "chiabuon.png";
            } else if (categoryName.contains("tri ân")) {
                imageName = "trian.png";
            }

            if (!imageName.isEmpty()) {
                try {
                    InputStream is = context.getAssets().open("danhmuc/" + imageName);
                    Drawable d = Drawable.createFromStream(is, null);
                    ivCategory.setImageDrawable(d);
                    is.close();
                } catch (Exception e) {
                    ivCategory.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                ivCategory.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}
