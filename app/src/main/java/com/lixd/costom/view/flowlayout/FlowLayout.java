package com.lixd.costom.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //记录每行宽度
    private int lineWidth;
    //记录每行高度
    private int lineHeight;
    //FlowLayout总宽度
    private int width;
    //FlowLayout总高度
    private int height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);


        height = 0;
        width = 0;
        lineWidth = 0;
        lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //获取子View的测量的宽高
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //计算子View的margin值
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            childWidth += layoutParams.leftMargin + layoutParams.rightMargin;
            childHeight += layoutParams.topMargin + layoutParams.bottomMargin;


            if (lineWidth + childWidth > measureWidth) {
                //需要换行处理
                height += lineHeight;
                width = Math.max(lineWidth, childWidth);

                lineHeight = childHeight;
                lineWidth = childWidth;

            } else {
                //
                lineWidth += childWidth;
                //
                lineHeight = Math.max(lineHeight, childHeight);
            }

            ////最后一行是不会超出width范围的，所以要单独处理
            if (i == childCount - 1) {
                width = Math.max(lineWidth, childWidth);
                height += lineHeight;
            }
            Log.e("FlowLayout", "onMeasure_lineHeight=" + lineHeight + ",i=" + i);
            Log.e("FlowLayout", "onMeasure_height=" + height);
            Log.e("FlowLayout", "-------");
        }
        //        Log.e("FlowLayout", "onMeasure_height=" + height);

        setMeasuredDimension(measureWidthMode == MeasureSpec.EXACTLY ? measureWidth : width,
                measureHeightMode == MeasureSpec.EXACTLY ? measureHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("FlowLayout", "onLayout_height=" + getMeasuredHeight());
        int childCount = getChildCount();
        int lineWidth = 0;
        int lineHeight = 0;
        int top = 0, left = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > getMeasuredWidth()) {
                //换行
                top += lineHeight;
                left = 0;

                //初始化
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            int lc = left + lp.leftMargin;
            int tc = top + lp.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();
            child.layout(lc, tc, rc, bc);

            left += childWidth;
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
}
