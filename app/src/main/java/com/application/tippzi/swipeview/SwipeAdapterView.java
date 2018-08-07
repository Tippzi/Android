package com.application.tippzi.swipeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.application.tippzi.Global.GD;
import com.application.tippzi.R;

public class SwipeAdapterView extends BaseSwipeAdapterView {

    private int MAX_VISIBLE = 4;
    private int MIN_ADAPTER_STACK = 6;
    private float ROTATION_DEGREES = 15.f;
    private Adapter mAdapter;
    private int LAST_OBJECT_IN_STACK = 0;
    private onSwipeListener mSwipeListener;
    private AdapterDataSetObserver mDataSetObserver;
    private boolean mInLayout = false;
    private View mActiveCard = null;
    private OnItemClickListener mOnItemClickListener;

    private SwipeCardListener swipeCardListener;
    private PointF mLastTouchPoint;

    public SwipeAdapterView(Context context) {
        this(context, null);
    }

    public SwipeAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.swipe_style);
    }

    public SwipeAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeAdapterView, defStyle, 0);
        MAX_VISIBLE = a.getInt(R.styleable.SwipeAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipeAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.SwipeAdapterView_rotation_degrees, ROTATION_DEGREES);
        a.recycle();
    }

    @Override
    public View getSelectedView() {
        return mActiveCard;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter == null) {
            return;
        }
        mInLayout = true;
        final int adapterCount = mAdapter.getCount();
        if (adapterCount == 0) {
            removeAllViewsInLayout();
        } else {
            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null && topCard != null && topCard == mActiveCard) {
                if (this.swipeCardListener.isTouching()) {
                    PointF lastPoint = this.swipeCardListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        this.mLastTouchPoint = lastPoint;
                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                    }
                }
            } else {
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }
        mInLayout = false;
        if (adapterCount <= MIN_ADAPTER_STACK) mSwipeListener.onAdapterAboutToEmpty(adapterCount);
    }

    private void layoutChildren(int startingIndex, int adapterCount) {
        while (startingIndex < Math.min(adapterCount, MAX_VISIBLE)) {
            View newUnderChild = mAdapter.getView(startingIndex, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                makeAndAddView(newUnderChild);
                LAST_OBJECT_IN_STACK = startingIndex;
            }
            startingIndex++;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void makeAndAddView(View child) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        addViewInLayout(child, 0, lp, true);
        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }
        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }
        child.layout(childLeft, childTop, childLeft + w, childTop + h);
    }

    private void setTopView() {
        if (getChildCount() > 0) {
            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null) {

                swipeCardListener = new SwipeCardListener(mActiveCard, mAdapter.getItem(0),
                        ROTATION_DEGREES, new SwipeCardListener.SwipeListener() {
                    @Override
                    public void onCardExited() {
                        mActiveCard = null;
                        mSwipeListener.removeFirstObjectInAdapter();
                    }

                    @Override
                    public void leftExit(Object dataObject) {
                        mSwipeListener.onLeftCardExit(dataObject);
                    }

                    @Override
                    public void rightExit(Object dataObject) {
                        mSwipeListener.onRightCardExit(dataObject);
                    }

                    @Override
                    public void onClick(Object dataObject) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClicked(dataObject);

                    }

                    @Override
                    public void onScroll(float scrollProgressPercent) {
                        mSwipeListener.onScroll(scrollProgressPercent);
                    }
                });

                mActiveCard.setOnTouchListener(swipeCardListener);
            }
        }
    }

    public SwipeCardListener getTopCardListener() throws NullPointerException {
        if (swipeCardListener == null) {
            throw new NullPointerException();
        }
        return swipeCardListener;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }
        mAdapter = adapter;
        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void setSwipeListener(onSwipeListener onSwipeListener) {
        this.mSwipeListener = onSwipeListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }



    public void undo() {
        mActiveCard = null;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }

    }



    public interface OnItemClickListener {
        void onItemClicked(Object dataObject);
    }


    public interface onSwipeListener {
        void removeFirstObjectInAdapter();

        void onLeftCardExit(Object dataObject);

        void onRightCardExit(Object dataObject);

        void onAdapterAboutToEmpty(int itemsInAdapter);

        void onScroll(float scrollProgressPercent);
    }


}