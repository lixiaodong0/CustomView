package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
