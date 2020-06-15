package com.xlab13.zcleaner.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.xlab13.zcleaner.CleanOptionsFragmentRV;
import com.xlab13.zcleaner.R;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.IconViewHolder> {

    OnItemClickListener mItemClickListener;

    List<CleanOptionsFragmentRV.IconButton> icons;

    public RVAdapter(List<CleanOptionsFragmentRV.IconButton> icons){
        this.icons = icons;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        IconViewHolder ivh = new IconViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        holder.buttonText.setText(icons.get(position).getTextId());
        holder.buttonIcon.setImageResource(icons.get(position).getIconBlId());
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public class IconViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView buttonText;
        ImageView buttonIcon;

        IconViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardView);
            buttonText = (TextView)itemView.findViewById(R.id.btn_text);
            buttonIcon = (ImageView) itemView.findViewById(R.id.btn_icon);

            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
