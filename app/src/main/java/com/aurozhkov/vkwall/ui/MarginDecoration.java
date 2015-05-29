package com.aurozhkov.vkwall.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginDecoration extends RecyclerView.ItemDecoration {

    private final int decorationSize;

    public MarginDecoration(float decorationSize) {
        this.decorationSize = (int) decorationSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewPosition();

        if (itemPosition == 0) {
            outRect.top = decorationSize;
        } else {
            outRect.top = 0;
        }
        outRect.bottom = decorationSize;
        outRect.left = decorationSize;
        outRect.right = decorationSize;
    }
}