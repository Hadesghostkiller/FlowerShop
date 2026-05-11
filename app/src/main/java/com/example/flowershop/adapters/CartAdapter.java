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
    private OnCartItemCheckListener checkListener;
    private OnQuantityChangeListener quantityListener;

    public interface OnCartItemDeleteListener {
        void onDeleteClick(CartItem item);
    }

    public interface OnCartItemCheckListener {
        void onItemCheckChanged(CartItem item, boolean isChecked);
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
    }

    public CartAdapter(Context context, List<CartItem> cartItemList,
                       OnCartItemDeleteListener deleteListener,
                       OnCartItemCheckListener checkListener,
                       OnQuantityChangeListener quantityListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.deleteListener = deleteListener;
        this.checkListener = checkListener;
        this.quantityListener = quantityListener;
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

        holder.cbItem.setOnCheckedChangeListener(null);
        holder.cbItem.setChecked(cartItem.isSelected());

        // Hiển thị Mã đơn hàng
        holder.tvMaDonHang.setText("Mã: " + cartItem.getMa_don_hang());

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
                    holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        }

        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            if (checkListener != null) {
                checkListener.onItemCheckChanged(cartItem, isChecked);
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = cartItem.getQuantity();
            if (currentQty > 1) {
                if (quantityListener != null) {
                    quantityListener.onQuantityChanged(cartItem, currentQty - 1);
                }
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (quantityListener != null) {
                quantityListener.onQuantityChanged(cartItem, cartItem.getQuantity() + 1);
            }
        });

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
        CheckBox cbItem;
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity, tvMaDonHang;
        ImageButton btnDelete, btnMinus, btnAdd;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.cbItemCart);
            ivImage = itemView.findViewById(R.id.ivCartImage);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvMaDonHang = itemView.findViewById(R.id.tvMaDonHang);
            btnDelete = itemView.findViewById(R.id.btnDeleteCartItem);
            btnMinus = itemView.findViewById(R.id.btnMinusCart);
            btnAdd = itemView.findViewById(R.id.btnAddCart);
        }
    }
}