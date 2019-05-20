package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 物流进度装饰
 */
public class LogisticsItemDecoration extends RecyclerView.ItemDecoration {
    //item左边空出来的最大间距
    private static final int MAX_LEFT_SPAC = 240;
    //绘制文本的画笔
    private Paint mTextPaint;
    //绘制圆的画笔
    private Paint mCirclePaint;
    //绘制线条的画笔
    private Paint mLinePaint;

    public LogisticsItemDecoration() {

        mTextPaint = new Paint();

        mCirclePaint = new Paint();

        mLinePaint = new Paint();
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        LogisticsAdapter adapter = (LogisticsAdapter) parent.getAdapter();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            draw(c, child, adapter.getData(i));
        }
    }

    private void draw(Canvas c, View child, LogisticsBean data) {
        //绘制图片
        drawIcon(c, child, data);
        //绘制线条
        drawLine(c, child);
        //绘制文本
        drawDateText(c, child, data);
    }

    private void drawIcon(Canvas c, View child, LogisticsBean data) {

    }

    private void drawLine(Canvas c, View child) {

    }

    private void drawDateText(Canvas c, View child, LogisticsBean data) {

    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = MAX_LEFT_SPAC;
    }
}
