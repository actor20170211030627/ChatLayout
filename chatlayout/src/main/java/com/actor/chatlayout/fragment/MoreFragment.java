package com.actor.chatlayout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actor.chatlayout.bean.ItemMore;
import com.actor.chatlayout.R;
import com.actor.chatlayout.RVItemDecoration;

import java.util.ArrayList;

/**
 * Description: 更多
 * Copyright  : Copyright (c) 2019
 * Author     : actor
 * Date       : 2019/6/2 on 20:07
 */
public class MoreFragment extends Fragment {

    public static final String              SPAN_COUNT = "SPAN_COUNT";
    public static final String              ITEM_DECORATION = "ITEM_DECORATION";
    public static final String              ITEMS = "ITEMS";
    private             int                 spanCount;
    private             int                 itemDecorationPx;
    private             ArrayList<ItemMore> items;
    private             OnItemClickListener mListener;
    private             RecyclerView        recyclerView;
    private             MoreAdapter         moreAdapter;

    public MoreFragment() {
    }

    /**
     * 官方获取实例
     * @param spanCount recyclerview 行数, 一般4行
     * @param itemDecorationPx item间距, 单位px
     * @param items 填充到 recyclerview 中的数据
     * @return
     */
    public static MoreFragment newInstance(int spanCount, int itemDecorationPx, ArrayList<ItemMore> items) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putInt(SPAN_COUNT, spanCount);
        args.putInt(ITEM_DECORATION, itemDecorationPx);
        args.putParcelableArrayList(ITEMS, items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//官方获取参数
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spanCount = getArguments().getInt(SPAN_COUNT);
            itemDecorationPx = getArguments().getInt(ITEM_DECORATION);
            items = getArguments().getParcelableArrayList(ITEMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_more, container, false);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
        moreAdapter = new MoreAdapter(items);
        recyclerView.addItemDecoration(new RVItemDecoration(itemDecorationPx, itemDecorationPx));
        recyclerView.setAdapter(moreAdapter);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnItemClickListener) {
//            mListener = (OnItemClickListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnItemClickListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        moreAdapter = null;
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ItemMore itemMore);
    }

    public class MoreAdapter extends RecyclerView.Adapter<MoreViewHolder> {

        private ArrayList<ItemMore> items;

        public MoreAdapter(ArrayList<ItemMore> items) {
            this.items = items;
        }

        @Override
        public MoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_chat_bottom, parent, false);
            return new MoreViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MoreViewHolder holder, int position) {
            ItemMore itemMore = items.get(position);
            holder.iv.setImageResource(itemMore.itemIcon);
            holder.tv.setText(itemMore.itemText);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if (mListener != null) mListener.onItemClick(pos, items.get(pos));
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv;
        public TextView  tv;

        public MoreViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
