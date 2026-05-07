package com.example.flowershop.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.model.CartItem;
import com.example.flowershop.model.SupabaseFlower;

import java.io.InputStream;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private OnCartItemDeleteListener deleteListener;
    private OnCartItemCheckListener checkListener; // THÊM MỚI: Lắng nghe sự kiện tick

    public interface OnCartItemDeleteListener {
        void onDeleteClick(CartItem item);
    }

    // THÊM MỚI: Interface cho sự kiện tick checkbox
    public interface OnCartItemCheckListener {
        void onItemCheckChanged(CartItem item, boolean isChecked);
    }

    public CartAdapter(Context context, List<CartItem> cartItemList,
                       OnCartItemDeleteListener deleteListener,
                       OnCartItemCheckListener checkListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.deleteListener = deleteListener;
        this.checkListener = checkListener;
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

        // 1. Gỡ bỏ listener cũ trước khi set state để tránh vòng lặp vô hạn
        holder.cbItem.setOnCheckedChangeListener(null);

        // 2. Set trạng thái checkbox hiện tại
        holder.cbItem.setChecked(cartItem.isSelected());

        if (flower != null) {
            holder.tvName.setText(flower.flowerName != null ? flower.flowerName : "Hoa chưa rõ tên");
            holder.tvPrice.setText(String.format("%,.0f VND", flower.price));

            if (flower.imageResource != null && !flower.imageResource.isEmpty()) {
                try {
                    String imagePath = "flower_image/" + flower.imageResource + ".png";
                    InputStream is = context.getAssets().open(imagePath);
                    Drawable d = Drawable.createFromStream(is, null);
                    holder.ivImage.setImageDrawable(d);
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        }

        holder.tvQuantity.setText("x" + cartItem.getQuantity());

        // 3. Gắn lại sự kiện khi người dùng click vào Checkbox
        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked); // Cập nhật model
            if (checkListener != null) {
                checkListener.onItemCheckChanged(cartItem, isChecked);
            }
        });

        // Sự kiện xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbItem; // Khai báo Checkbox
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.cbItemCart); // Ánh xạ
            ivImage = itemView.findViewById(R.id.ivCartImage);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnDelete = itemView.findViewById(R.id.btnDeleteCartItem);
        }
    }
}