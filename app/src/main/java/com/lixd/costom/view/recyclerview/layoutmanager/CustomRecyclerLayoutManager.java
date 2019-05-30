package com.lixd.costom.view.recyclerview.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 自定义回收的LayoutManager
 */
public class CustomRecyclerLayoutManager extends RecyclerView.LayoutManager {
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
            Rect rect = mItemRects.get(i);
            View childShowView = recycler.getViewForPosition(i);
            addView(childShowView);
            measureChildWithMargins(childShowView, 0, 0);
            layoutDecorated(childShowView, rect.left, rect.top, rect.right, rect.bottom);
        }

        //当所有item的高度没有超过 RecyclerView高度时, 取RecyclerView高度
        mTotalHeight = Math.max(top, getVerticalSpace());
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
    private int mTotalScrollHeight;
    //Item总高度
    private int mTotalHeight;

    /**
     * 纵向滑动的偏移量
     * dy>0 手指向上滚动 界面所有内容需要上移
     * dy<0 手指向下滚动 界面所有内容需要下移
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            return dy;
        }

        int scrollOffset = dy;

        if (mTotalScrollHeight + dy < 0) {
            //滑动到顶部 需要越界处理
            scrollOffset = -mTotalScrollHeight;
        } else if (mTotalScrollHeight + dy > mTotalHeight - getVerticalSpace()) {
            //滑动到底部 需要越界处理
            scrollOffset = mTotalHeight - getVerticalSpace() - mTotalScrollHeight;
        }

        //回收越界的子View
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (scrollOffset > 0) {
                //需要回收当前屏幕，上越界的View
                if (getDecoratedBottom(child) - scrollOffset < 0) {
                    removeAndRecycleView(child, recycler);
                    continue;
                }
            } else if (scrollOffset < 0) {
                //需要回收当前屏幕，下越界的View
                if (getDecoratedTop(child) - scrollOffset > getHeight() - getPaddingBottom()) {
                    removeAndRecycleView(child, recycler);
                    continue;
                }
            }
        }

        //布局子View
        //获取屏幕可见的距离
        Rect visibleArea = getVisibleArea(scrollOffset);
        if (scrollOffset >= 0) {
            //获取屏幕最后一个View
            View lastView = getChildAt(getChildCount() - 1);
            //获取最后一个View下一个View
            int nextPosition = getPosition(lastView) + 1;
            //从下一个View开始遍历,如果有相交部分就布局出来
            for (int i = nextPosition; i <= getItemCount() - 1; i++) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(visibleArea, rect)) {
                    View showView = recycler.getViewForPosition(i);
                    addView(showView);
                    measureChildWithMargins(showView, 0, 0);
                    layoutDecorated(showView, rect.left, rect.top - mTotalScrollHeight, rect.right, rect.bottom - mTotalScrollHeight);
                } else {
                    break;
                }
            }
        } else {
            //获取屏幕第一个View
            View firstView = getChildAt(0);
            //获取第一个View的上一个
            int maxPosition = getPosition(firstView) - 1;
            //从下一个View开始遍历,如果有相交部分就布局出来
            for (int i = maxPosition; i >= 0; i--) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(visibleArea, rect)) {
                    View showView = recycler.getViewForPosition(i);
                    addView(showView,0);
                    measureChildWithMargins(showView, 0, 0);
                    layoutDecorated(showView, rect.left, rect.top - mTotalScrollHeight, rect.right, rect.bottom - mTotalScrollHeight);
                } else {
                    break;
                }
            }
        }

        mTotalScrollHeight += scrollOffset;
        //offsetChildrenVertical() 用于滚动所有的子View一段距离
        offsetChildrenVertical(-scrollOffset);
        return scrollOffset;
    }

    /**
     * 获取可见的区域
     *
     * @param dy 当前滚动的偏移量
     * @return
     */
    private Rect getVisibleArea(int dy) {
        return new Rect(getPaddingLeft(),
                getPaddingTop() + mTotalScrollHeight + dy,
                getWidth() + getPaddingRight(),
                getVerticalSpace() + mTotalScrollHeight + dy);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
