package com.lixd.costom.view.recyclerview.layoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 自定义回收的LayoutManager 2
 */
public class CustomRecyclerLayoutManager2 extends RecyclerView.LayoutManager {
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

        //1.首先拿到屏幕最后一个跟第一个的View
        View lastView = getChildAt(getChildCount() - 1);
        View firstView = getChildAt(0);
        //2.清空屏幕
        detachAndScrapAttachedViews(recycler);
        mTotalScrollHeight += scrollOffset;
        //获取屏幕可见的距离
        Rect visibleArea = getVisibleArea();

        if (scrollOffset >= 0) {
            int minPosition = getPosition(firstView);
            //从下一个View开始遍历,如果有相交部分就布局出来
            for (int i = minPosition; i < getItemCount(); i++) {
                insertView(i, visibleArea, recycler, false);
            }
        } else {
            int maxPosition = getPosition(lastView);
            //从下一个View开始遍历,如果有相交部分就布局出来
            for (int i = maxPosition; i >= 0; i--) {
                insertView(i, visibleArea, recycler, true);
            }
        }
        return scrollOffset;
    }

    private void insertView(int position, Rect visibleArea, RecyclerView.Recycler recycler, boolean firstPos) {
        Rect rect = mItemRects.get(position);
        if (Rect.intersects(visibleArea, rect)) {
            View showView = recycler.getViewForPosition(position);
            if (firstPos) {
                addView(showView, 0);
            } else {
                addView(showView);
            }
            measureChildWithMargins(showView, 0, 0);
            layoutDecorated(showView, rect.left, rect.top - mTotalScrollHeight, rect.right, rect.bottom - mTotalScrollHeight);
            //布局完成 让View执行Y轴动画
            showView.setRotationY(showView.getRotationY() + 1);
        }
    }

    /**
     * 获取可见的区域
     *
     * @return
     */
    private Rect getVisibleArea() {
        return new Rect(getPaddingLeft(),
                getPaddingTop() + mTotalScrollHeight,
                getWidth() + getPaddingRight(),
                getVerticalSpace() + mTotalScrollHeight);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
