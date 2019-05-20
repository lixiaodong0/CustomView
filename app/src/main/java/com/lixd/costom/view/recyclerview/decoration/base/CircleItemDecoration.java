package com.lixd.costom.view.recyclerview.decoration.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CircleItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            int leftDecorationWidth = layoutManager.getLeftDecorationWidth(child);
            c.drawCircle(leftDecorationWidth / 2, child.getBottom() - child.getHeight() / 2, 10, paint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = 100;
    }
}
