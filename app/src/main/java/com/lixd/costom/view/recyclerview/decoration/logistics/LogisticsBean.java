package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.support.annotation.DrawableRes;

public class LogisticsBean {
    public String date;
    public String time;
    public String title;
    public String detail;
    public Node node;

    public LogisticsBean(String date, String time, String title, String detail, Node node) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.detail = detail;
        this.node = node;
    }

    public static class Node {
        //节点的本地资源图片,如果没有将绘制圆代替
        @DrawableRes
        public int iconRes;
        //当前节点是否是选中的
        public boolean isSelected;
        //是否有下一个节点
        public boolean isNextNode;
        //节点流程是否全部完成
        public boolean isFinish;

        public Node(int iconRes, boolean isSelected, boolean isNextNode, boolean isFinish) {
            this.iconRes = iconRes;
            this.isSelected = isSelected;
            this.isNextNode = isNextNode;
            this.isFinish = isFinish;
        }

        public Node(int iconRes, boolean isSelected) {
            this(iconRes, isSelected, true, false);
        }

    }
}
