package com.application.tippzi.swipeview;

/**
 * Created by mukeshsolanki on 01/11/17.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;

abstract class BaseSwipeAdapterView extends AdapterView {

    private int heightMeasureSpec;
    private int widthMeasureSpec;

    public BaseSwipeAdapterView(Context context) {
        super(context);
    }

    public BaseSwipeAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSwipeAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int i) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
    }

    public int getWidthMeasureSpec() {
        return widthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return heightMeasureSpec;
    }


}