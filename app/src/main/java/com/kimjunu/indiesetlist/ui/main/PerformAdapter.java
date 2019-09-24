package com.kimjunu.indiesetlist.ui.main;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.PerformModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class PerformAdapter extends StatelessSection {
    private static final String TAG = "PerformAdapter";

    Context mContext;
    String mTitle;
    ArrayList<PerformModel> mPerformList;

    private OnPerformSelectListener performSelectListener = null;

    public interface OnPerformSelectListener {
        void onPerformSelected(PerformModel perform);
    }

    public void setOnPerformSelectListener(OnPerformSelectListener listener) {
        performSelectListener = listener;
    }

    public class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvHeader)
        public TextView tvHeader;

        @BindView(R.id.ivExpand)
        public ImageView ivExpand;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public class PerformViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardPerform)
        CardView cardPerform;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvArtist)
        TextView tvArtist;

        @BindView(R.id.tvVenue)
        TextView tvVenue;

        public PerformViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.cardPerform)
        public void onCardPerformClicked(View view) {
            int position = (int) view.getTag();
            if (position >= 0) {
                if (performSelectListener != null)
                    performSelectListener.onPerformSelected(mPerformList.get(position));
            }
        }
    }

    public PerformAdapter(Context context, String title, ArrayList<PerformModel> list) {
        super(R.layout.list_header, R.layout.item_perform);

        mContext = context;
        mTitle = title;
        mPerformList = list;
    }

    @Override
    public int getContentItemsTotal() {
        return mPerformList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PerformViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        PerformViewHolder iHolder = (PerformViewHolder) holder;

        PerformModel item = mPerformList.get(position);
        iHolder.tvTitle.setText(item.videoTitle);
        iHolder.tvArtist.setText(item.artist);
        iHolder.tvVenue.setText(item.venue);
        iHolder.cardPerform.setTag(position);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new ListHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        ListHeaderViewHolder hHolder = (ListHeaderViewHolder) holder;

        hHolder.tvHeader.setText(mTitle);
    }
}