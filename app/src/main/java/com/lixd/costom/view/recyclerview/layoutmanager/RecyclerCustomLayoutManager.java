package com.lixd.costom.view.recyclerview.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 自定义回收的LayoutManager
 */
public class RecyclerCustomLayoutManager extends RecyclerView.LayoutManager {
    //保存所有item的布局位置
    private SparseArray<Rect> mItemRects = new SparseArray<>();

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int itemCount = getItemCount();
        if (itemCount == 0) {
            //如果没有itemView,清空屏幕,防止容错率
            detachAndScrapAttachedViews(recycler);
            return;
        }

        //清空屏幕所有的item,放置mAttachedScrap列表中
        detachAndScrapAttachedViews(recycler);

        //假设所有的item大小是一致的,取第一个item的高度
        View child = recycler.getViewForPosition(0);
        measureChildWithMargins(child, 0, 0);
        int itemWidth = getDecoratedMeasuredWidth(child);
        int itemHeight = getDecoratedMeasuredHeight(child);
        //用RecyclerView高度 / item高度 = 一屏幕可以放多少个item
        int visibleCount = getVerticalSpace() / itemHeight;

        int top = 0;
        //计算所有View的位置摆放的位置
        for (int i = 0; i < itemCount; i++) {
            Rect rect = new Rect(0, top, itemWidth, top + itemHeight);
            mItemRects.put(i, rect);
            top += itemHeight;
        }

        //布置可见的HolderView
        for (int i = 0; i < visibleCount; i++) {
            View childShowView = recycler.getViewForPosition(i);
            measureChildWithMargins(childShowView, 0, 0);
            Rect rect = mItemRects.get(i);
            layoutDecorated(childShowView, rect.left, rect.top, rect.right, rect.bottom);
        }

        //当所有item的高度没有超过 RecyclerView高度时, 取RecyclerView高度
        totalHeight = Math.max(top, getVerticalSpace());
    }

    /**
     * RecyclerView的总高度
     *
     * @return
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 纵向是否可以滑动
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    //滑动的总高度
    private int totalScrollHeight;
    //Item总高度
    private int totalHeight;

    /**
     * 纵向滑动的偏移量
     * dy>0 手指向上滚动 界面所有内容需要上移
     * dy<0 手指向下滚动 界面所有内容需要下移
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollOffset = dy;
        if (totalScrollHeight + scrollOffset < 0) {
            //滑动到顶部 需要越界处理
            scrollOffset = -totalScrollHeight;
        } else if (totalScrollHeight + scrollOffset > totalHeight - getVerticalSpace()) {
            //滑动到底部 需要越界处理
            scrollOffset = totalHeight - getVerticalSpace() - totalScrollHeight;
        }
        totalScrollHeight += scrollOffset;
        //offsetChildrenVertical() 用于滚动所有的子View一段距离
        offsetChildrenVertical(-scrollOffset);
        return dy;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
