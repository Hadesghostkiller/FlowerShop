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

import com.example.flowershop.R;
import com.example.flowershop.model.CartItem;
import com.example.flowershop.model.SupabaseFlower;

import java.io.InputStream;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private OnCartItemDeleteListener deleteListener;

    // Interface để báo cho Activity biết khi người dùng bấm xóa
    public interface OnCartItemDeleteListener {
        void onDeleteClick(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItemList, OnCartItemDeleteListener listener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.deleteListener = listener;
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

            // Load ảnh từ thư mục assets/flower_image/
            if (flower.imageResource != null && !flower.imageResource.isEmpty()) {
                try {
                    // Tương tự ở đây cũng sửa thành imageResource
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

        // Bắt sự kiện bấm nút Xóa
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
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartImage);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnDelete = itemView.findViewById(R.id.btnDeleteCartItem);
        }
    }
}