package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.support.annotation.DrawableRes;

public class LogisticsBean {
    public String date;
    public String time;
    public String title;
    public String detail;
    public Node node;

    public static class Node {
        //节点的本地资源图片,如果没有将绘制圆代替
        @DrawableRes
        public int iconRes;
        //当前节点是否是选中的
        public boolean isSelected;
        //是否有下一个节点
        public boolean isNext;
    }
}
