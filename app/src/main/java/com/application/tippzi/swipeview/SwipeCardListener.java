package com.application.tippzi.swipeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.application.tippzi.Global.GD;

public class SwipeCardListener implements View.OnTouchListener {

    private static final String TAG = SwipeCardListener.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;
    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final SwipeListener mSwipeListener;
    private final Object dataObject;
    private final float halfWidth;
    private float BASE_ROTATION_DEGREES;
    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));

    SwipeCardListener(View frame, Object itemAtPosition, float rotation_degrees, SwipeListener swipeListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mSwipeListener = swipeListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (GD.select_user_type.equals("business")) {
            if (GD.businessModel.bars.get(0).deals.size() != 1) {
                if (GD.slide_index == GD.businessModel.bars.get(0).deals.size()) {

                } else {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            mActivePointerId = event.getPointerId(0);
                            float x = 0;
                            float y = 0;
                            boolean success = false;
                            try {
                                x = event.getX(mActivePointerId);
                                y = event.getY(mActivePointerId);
                                success = true;
                            } catch (IllegalArgumentException e) {
                                Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                            }
                            if (success) {
                                aDownTouchX = x;
                                aDownTouchY = y;
                                if (aPosX == 0) {
                                    aPosX = frame.getX();
                                }
                                if (aPosY == 0) {
                                    aPosY = frame.getY();
                                }
                                if (y < objectH / 2) {
                                    touchPosition = 0;
                                } else {
                                    touchPosition = TOUCH_BELOW;
                                }
                            }
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            mActivePointerId = INVALID_POINTER_ID;
                            resetCardViewOnStack();
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            final int pointerIndex = (event.getAction() &
                                    MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                            final int pointerId = event.getPointerId(pointerIndex);
                            if (pointerId == mActivePointerId) {
                                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                mActivePointerId = event.getPointerId(newPointerIndex);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                            final float xMove = event.getX(pointerIndexMove);
                            final float yMove = event.getY(pointerIndexMove);
                            final float dx = xMove - aDownTouchX;
                            final float dy = yMove - aDownTouchY;
                            aPosX += dx;
                            aPosY += dy;
                            float distobjectX = aPosX - objectX;
                            float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                            if (touchPosition == TOUCH_BELOW) {
                                rotation = -rotation;
                            }
                            frame.setX(aPosX);
                            frame.setY(aPosY);
                            frame.setRotation(rotation);
                            mSwipeListener.onScroll(getScrollProgressPercent());
                            break;
                        case MotionEvent.ACTION_CANCEL: {
                            mActivePointerId = INVALID_POINTER_ID;
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                        }
                    }
                }
            }
        } else if (GD.select_user_type.equals("customer")){
                if (GD.slide_index == GD.barModels.get(GD.select_pos).deals.size()) {

                } else {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            mActivePointerId = event.getPointerId(0);
                            float x = 0;
                            float y = 0;
                            boolean success = false;
                            try {
                                x = event.getX(mActivePointerId);
                                y = event.getY(mActivePointerId);
                                success = true;
                            } catch (IllegalArgumentException e) {
                                Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                            }
                            if (success) {
                                aDownTouchX = x;
                                aDownTouchY = y;
                                if (aPosX == 0) {
                                    aPosX = frame.getX();
                                }
                                if (aPosY == 0) {
                                    aPosY = frame.getY();
                                }
                                if (y < objectH / 2) {
                                    touchPosition = 0;
                                } else {
                                    touchPosition = TOUCH_BELOW;
                                }
                            }
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            mActivePointerId = INVALID_POINTER_ID;
                            resetCardViewOnStack();
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            final int pointerIndex = (event.getAction() &
                                    MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                            final int pointerId = event.getPointerId(pointerIndex);
                            if (pointerId == mActivePointerId) {
                                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                mActivePointerId = event.getPointerId(newPointerIndex);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                            final float xMove = event.getX(pointerIndexMove);
                            final float yMove = event.getY(pointerIndexMove);
                            final float dx = xMove - aDownTouchX;
                            final float dy = yMove - aDownTouchY;
                            aPosX += dx;
                            aPosY += dy;
                            float distobjectX = aPosX - objectX;
                            float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                            if (touchPosition == TOUCH_BELOW) {
                                rotation = -rotation;
                            }
                            frame.setX(aPosX);
                            frame.setY(aPosY);
                            frame.setRotation(rotation);
                            mSwipeListener.onScroll(getScrollProgressPercent());
                            break;
                        case MotionEvent.ACTION_CANCEL: {
                            mActivePointerId = INVALID_POINTER_ID;
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                        }
                    }
                }
        }
        return true;
    }

    private float getScrollProgressPercent() {
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private void resetCardViewOnStack() {
        if (movedBeyondLeftBorder()) {
            onSelected(true, getExitPoint(-objectW), 100);
            mSwipeListener.onScroll(-1.0f);
        } else if (movedBeyondRightBorder()) {
            onSelected(false, getExitPoint(parentWidth), 100);
            mSwipeListener.onScroll(1.0f);
        } else {
            float abslMoveDistance = Math.abs(aPosX - objectX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .x(objectX)
                    .y(objectY)
                    .rotation(0);
            mSwipeListener.onScroll(0.0f);
            if (abslMoveDistance < 4.0) {
                mSwipeListener.onClick(dataObject);
            }
        }
    }

    private boolean movedBeyondLeftBorder() {
        return aPosX + halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder() {
        return aPosX + halfWidth > rightBorder();
    }


    private float leftBorder() {
        return parentWidth / 4.f;
    }

    private float rightBorder() {
        return 3 * parentWidth / 4.f;
    }


    private void onSelected(final boolean isLeft,
                            float exitY, long duration) {
        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }
        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(exitX)
                .y(exitY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isLeft) {
                            mSwipeListener.onCardExited();
                            mSwipeListener.leftExit(dataObject);
                        } else {

                            mSwipeListener.onCardExited();
                            mSwipeListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }
                })
                .rotation(getExitRotation(isLeft));
    }

    public void selectLeft() {
        if (!isAnimationRunning)
            onSelected(true, objectY, 200);
    }

    public void selectRight() {
        if (!isAnimationRunning)
            onSelected(false, objectY, 200);
    }

    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;
        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;
        LinearRegression regression = new LinearRegression(x, y);
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }

    boolean isTouching() {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    PointF getLastPoint() {
        return new PointF(this.aPosX, this.aPosY);
    }

    protected interface SwipeListener {
        void onCardExited();

        void leftExit(Object dataObject);

        void rightExit(Object dataObject);

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);
    }

}
