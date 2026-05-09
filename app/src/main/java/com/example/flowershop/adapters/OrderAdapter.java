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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // 1. Hiển thị Tên hoa (Lấy từ order_details và làm sạch ký hiệu số lượng)
        if (order.order_details != null) {
            String flowerName = order.order_details.replaceAll("\\(x\\d+\\)", "").replace(",", "").trim();
            holder.tvFlowerDetails.setText(flowerName);
        }

        // 2. Hiển thị Ngày đặt
        if (order.created_at != null && order.created_at.length() >= 10) {
            holder.tvOrderDate.setText("Ngày đặt: " + order.created_at.substring(0, 10));
        }

        // 3. Hiển thị Trạng thái
        holder.tvStatus.setText(order.status);

        // 4. Trích xuất số lượng và hiển thị cùng PTTT
        String quantity = "1";
        if (order.order_details != null && order.order_details.contains("(x")) {
            try {
                int start = order.order_details.indexOf("(x") + 2;
                int end = order.order_details.indexOf(")", start);
                quantity = order.order_details.substring(start, end);
            } catch (Exception e) {
                quantity = "1";
            }
        }
        holder.tvPaymentMethod.setText("Số lượng: " + quantity + " | " + order.payment_method);

        // 5. Hiển thị Tổng cộng
        holder.tvTotalAmount.setText(String.format("%,.0f VND", order.total_amount));
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        // Đã sửa tvOrderId thành tvFlowerDetails cho đúng với XML
        TextView tvOrderDate, tvStatus, tvFlowerDetails, tvPaymentMethod, tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvFlowerDetails = itemView.findViewById(R.id.tvFlowerDetails);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}