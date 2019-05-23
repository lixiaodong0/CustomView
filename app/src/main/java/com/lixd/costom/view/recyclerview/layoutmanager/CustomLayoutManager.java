package com.lixd.costom.view.recyclerview.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class CustomLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int itemCount = getItemCount();
        int top = 0;
        for (int i = 0; i < itemCount; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int measuredWidth = getDecoratedMeasuredWidth(child);
            int measuredHeight = getDecoratedMeasuredHeight(child);
            layoutDecorated(child, 0, top, measuredWidth, top + measuredHeight);
            top += measuredHeight;
        }
        //Math.max(top, getVerticalSpace()); 子控件总高度 跟 RecyclerView的高度相比较 取最大值
        totalHeight = Math.max(top, getVerticalSpace());
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private int totalScrollHeight;

    private int totalHeight;

    /**
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
        //保存滚动的总高度
        return dy;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
