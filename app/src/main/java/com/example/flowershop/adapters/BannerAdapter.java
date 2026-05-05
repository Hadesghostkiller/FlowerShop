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
import com.example.flowershop.model.Banner;
import java.io.InputStream;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Banner> banners;
    private Context context;

    public BannerAdapter(Context context, List<Banner> banners) {
        this.context = context;
        this.banners = banners;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.tvDesc.setText(banner.description);

        try {
            String path = "banner/" + banner.image_name + ".jpg";
            InputStream is = context.getAssets().open(path);
            Drawable d = Drawable.createFromStream(is, null);
            holder.img.setImageDrawable(d);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return banners != null ? banners.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvDesc;
        BannerViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.imgBannerItem);
            tvDesc = v.findViewById(R.id.tvBannerDescription);
        }
    }
}