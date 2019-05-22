package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lixd.costom.view.R;

import java.util.List;

public class LogisticsAdapter extends RecyclerView.Adapter<LogisticsAdapter.ViewHolder> {
    public List<LogisticsBean> mData;
    public Context mContext;

    public LogisticsAdapter(List<LogisticsBean> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_logistics_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LogisticsBean data = getData(i);

        viewHolder.tvTitle.setTextColor(data.node.isSelected ? Color.parseColor("#000000") : Color.parseColor("#8a8a8a"));
        viewHolder.tvDetail.setTextColor(data.node.isSelected ? Color.parseColor("#000000") : Color.parseColor("#8a8a8a"));
        viewHolder.tvTitle.setText(data.title);
        viewHolder.tvTitle.setVisibility(TextUtils.isEmpty(data.title) ? View.GONE : View.VISIBLE);
        viewHolder.tvDetail.setText(data.detail);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public List<LogisticsBean> getData() {
        return mData;
    }

    public LogisticsBean getData(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvTitle;
        public final TextView tvDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDetail = itemView.findViewById(R.id.tv_detail);
        }
    }
}
