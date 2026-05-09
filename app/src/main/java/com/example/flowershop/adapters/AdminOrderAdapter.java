package com.example.flowershop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flowershop.R;
import com.example.flowershop.model.Order;
import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder> {

    private List<Order> orderList;

    public AdminOrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item_admin_order mới
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new AdminOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Hiển thị danh sách hoa/Sản phẩm
        if (order.order_details != null && !order.order_details.isEmpty()) {
            holder.tvOrderDetails.setText(order.order_details);
        } else {
            holder.tvOrderDetails.setText("Không có thông tin hoa");
        }

        // Hiển thị thông tin khách hàng
        holder.tvCustomerName.setText(order.customer_name);
        holder.tvPhone.setText("SĐT: " + order.phone);
        holder.tvAddress.setText("Địa chỉ: " + order.address);

        // Hiển thị phương thức thanh toán
        holder.tvPaymentMethod.setText("PTTT: " + order.payment_method);

        // Hiển thị tổng tiền định dạng VND
        holder.tvTotalAmount.setText(String.format("%,.0fđ", order.total_amount));

        // Trạng thái mặc định là hoàn tất như yêu cầu của bạn
        holder.tvStatusLabel.setText("Trạng thái: " + (order.status != null ? order.status : "Hoàn tất"));
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDetails, tvCustomerName, tvPhone, tvAddress, tvPaymentMethod, tvTotalAmount, tvStatusLabel;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDetails = itemView.findViewById(R.id.tvAdminOrderDetails);
            tvCustomerName = itemView.findViewById(R.id.tvAdminCustomerName);
            tvPhone = itemView.findViewById(R.id.tvAdminPhone);
            tvAddress = itemView.findViewById(R.id.tvAdminAddress);
            tvPaymentMethod = itemView.findViewById(R.id.tvAdminPaymentMethod);
            tvTotalAmount = itemView.findViewById(R.id.tvAdminTotalAmount);
            tvStatusLabel = itemView.findViewById(R.id.tvAdminStatusLabel);
        }
    }
}