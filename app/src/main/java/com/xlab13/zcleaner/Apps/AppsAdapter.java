package com.xlab13.zcleaner.Apps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xlab13.zcleaner.R;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {
    Context context;

    private List<AppItem> items;

    public AppsAdapter(Context context, List<AppItem> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        AppViewHolder holder = new AppViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppItem item = items.get(position);

        holder.ivPic.setImageBitmap(item.Image);
        holder.tvTitle.setText(item.Title);
        holder.tvText.setText(item.Description);

        holder.rlCont.setOnClickListener((v)->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id="+item.PackageName));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvText;
        ImageView ivPic;
        RelativeLayout rlCont;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAppTitle);
            tvText = itemView.findViewById(R.id.tvAppText);

            rlCont = itemView.findViewById(R.id.clAppCont);

            ivPic = itemView.findViewById(R.id.ivAppPic);
        }

    }
}