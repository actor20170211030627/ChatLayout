package com.actor.chatlayout.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actor.chatlayout.R;
import com.actor.chatlayout.bean.ItemMore;
import com.actor.myandroidframework.fragment.ActorBaseFragment;
import com.actor.myandroidframework.widget.BaseItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 更多
 * Author     : 李大发
 * Date       : 2019/6/2 on 20:07
 */
public class MoreFragment extends ActorBaseFragment {

    public static final String              SPAN_COUNT = "SPAN_COUNT";
    public static final String              ITEM_DECORATION = "ITEM_DECORATION";
    public static final String              ITEMS = "ITEMS";
    private             int                 spanCount;
    private             int                 itemDecorationPx;
    private             ArrayList<ItemMore> items;
    private             OnItemClickListener mListener;
    private             RecyclerView        recyclerView;
    private             MoreAdapter         moreAdapter;

    /**
     * 获取实例
     * @param spanCount recyclerview 行数, 一般4行
     * @param itemDecorationPx item间距, 单位px
     * @param items 填充到 recyclerview 中的数据
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

    //获取参数
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            spanCount = arguments.getInt(SPAN_COUNT);
            itemDecorationPx = arguments.getInt(ITEM_DECORATION);
            items = arguments.getParcelableArrayList(ITEMS);
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
        moreAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mListener != null) mListener.onItemClick(position, items.get(position));
            }
        });
        recyclerView.addItemDecoration(new BaseItemDecoration(itemDecorationPx, itemDecorationPx));
        recyclerView.setAdapter(moreAdapter);
    }

    /**
     * 设置 Item 点击监听
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ItemMore itemMore);
    }

    //Adapter
    protected class MoreAdapter extends BaseQuickAdapter<ItemMore, BaseViewHolder> {
        protected MoreAdapter(@Nullable List<ItemMore> data) {
            super(R.layout.item_chat_bottom, data);
        }
        @Override
        protected void convert(@NonNull BaseViewHolder helper, ItemMore item) {
            helper.setText(R.id.tv, item.itemText).setImageResource(R.id.iv, item.itemIcon);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
        moreAdapter = null;
    }
}
