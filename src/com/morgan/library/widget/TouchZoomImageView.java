package com.morgan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 一个带有回弹动画的可缩放的图片组件。
 * 
 * @author jgc 2013-6-16
 */
public class TouchZoomImageView extends ImageView {
    // 表示当前没有状态
    static final int NONE = 0;
    // 表示当前处于移动状态
    static final int DRAG = 1;
    // 表示当前处于缩放状态
    static final int ZOOM = 2;
    // mode用于标示当前处于什么状态
    private int mMode = NONE;

    // 表示放大图片
    static final int ZOOM_BIGGER = 3;
    // 表示缩小图片
    static final int ZOOM_SMALLER = 4;

    // 第一次触摸两点的距离
    private float mBeforeLenght;
    // 移动后两点的距离
    private float mAfterLenght;
    // 缩放因子
    private float mZoomScale = 0.04f;

    // 图片的移动范围，也就是linearLayout的范围(一般是屏幕大小)
    private int mParentW;
    private int mParentH;
    private boolean mInitParentSize = false;

    // 开始触摸点
    private int mStartX;
    private int mStartY;

    // 结束触摸点
    private int mStopX;
    private int mStopY;
    // 回弹动画
    private TranslateAnimation mAnimation;
    // 多点触控是都都落到当前组件上(不知道为什么即使第二个点没有落在当前控件上也会触发当前控件的事件)
    private boolean mIsMulti;
    // 设置一个最小值以便能够双指缩放
    private static final int MIN_LENGTH = 150;

    public TouchZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setPadding(0, 0, 0, 0);
        this.setScaleType(ScaleType.FIT_XY);
        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    // 用来计算2个触摸点的距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInitParentSize) {
            View parentView = (View) getParent();
            mParentW = parentView.getWidth();
            mParentH = parentView.getHeight();
            mInitParentSize = true;
        }
        // MotionEvent.ACTION_MASK表示多点触控事件
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            mMode = DRAG;
            // 表示相对于屏幕左上角为原点的坐标，getX是相对于父组件,用(int)event.getX()一样,表示相对于父组件的坐标，这里就是相对于自定义imageView左上角的坐标.
            // 建议用前者，如果不是全屏拖动，而是指定范围内，一样适用！如果用getX()，图片移动过程中会出现抖动
            mStopX = (int) event.getRawX();
            mStopY = (int) event.getRawY();
            // 这里使用了一个小技巧哦（因为如果只记忆x和y的值，那么start和stop的差值只是位移，这样每次重新设置位置后都要重新给start和stop赋值，不然下次设置位置会有问题，因为getLeft的值会变）
            mStartX = mStopX - this.getLeft();
            mStartY = mStopY - this.getTop();
            // 默认生效
            mIsMulti = true;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            // 防止指纹间的距离
            if (spacing(event) > 10f) {
                mMode = ZOOM;
                mBeforeLenght = spacing(event);
                // 判断第二个点是否落在当前控件上，没有的话多点触控不生效
                if (event.getX(1) < getWidth() && event.getX(1) > 0 && event.getY(1) > 0 && event.getY(1) < getHeight()) {
                    mIsMulti = true;
                } else {
                    mIsMulti = false;
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            // 如果图片缩放到宽高任意一个小于150，那么自动放大，直到大于150，使能够多点触控
            while (getHeight() < MIN_LENGTH || getWidth() < MIN_LENGTH) {
                setScale(mZoomScale, ZOOM_BIGGER);
            }
            int disX = 0;
            int disY = 0;
            if (getHeight() <= mParentH) {
                if (this.getTop() < 0) {
                    disY = getTop();
                    this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                } else if (this.getBottom() > mParentH) {
                    disY = getBottom() - mParentH;
                    this.layout(this.getLeft(), mParentH - getHeight(), this.getRight(), mParentH);
                }
            } else {
                int Y1 = getTop();
                int Y2 = getHeight() - mParentH + getTop();
                if (Y1 > 0) {
                    disY = Y1;
                    this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                } else if (Y2 < 0) {
                    disY = Y2;
                    this.layout(this.getLeft(), mParentH - getHeight(), this.getRight(), mParentH);
                }
            }
            if (getWidth() <= mParentW) {
                if (this.getLeft() < 0) {
                    disX = getLeft();
                    this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
                } else if (this.getRight() > mParentW) {
                    disX = getWidth() - mParentW + getLeft();
                    this.layout(mParentW - getWidth(), this.getTop(), mParentW, this.getBottom());
                }
            } else {
                int X1 = getLeft();
                int X2 = getWidth() - mParentW + getLeft();
                if (X1 > 0) {
                    disX = X1;
                    this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
                } else if (X2 < 0) {
                    disX = X2;
                    this.layout(mParentW - getWidth(), this.getTop(), mParentW, this.getBottom());
                }
            }
            // 根据disX和disY的偏移量采用移动动画回弹归位，动画时间为500毫秒。
            if (disX != 0 || disY != 0) {
                mAnimation = new TranslateAnimation(disX, 0, disY, 0);
                mAnimation.setDuration(500);
                this.startAnimation(mAnimation);
            }
            mMode = NONE;
            break;
        case MotionEvent.ACTION_POINTER_UP:
            mMode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mMode == DRAG) {
                // 表示相对于屏幕左上角为原点的坐标
                mStopX = (int) event.getRawX();
                mStopY = (int) event.getRawY();
                // 执行拖动事件的时，不断变换自定义imageView的位置从而达到拖动效果
                this.setPosition(mStopX - mStartX, mStopY - mStartY, mStopX + this.getWidth() - mStartX, mStopY
                        - mStartY + this.getHeight());
            } else if (mMode == ZOOM && mIsMulti) {
                if (spacing(event) > 10f) {
                    mAfterLenght = spacing(event);
                    float gapLenght = mAfterLenght - mBeforeLenght;
                    if (gapLenght == 0) {
                        break;
                    }
                    // 图片宽度（也就是自定义imageView）必须大于70才可以缩放
                    else if (Math.abs(gapLenght) > 5f && getWidth() > 70) {
                        if (gapLenght > 0) {
                            this.setScale(mZoomScale, ZOOM_BIGGER);
                        } else {
                            this.setScale(mZoomScale, ZOOM_SMALLER);
                        }
                        mBeforeLenght = mAfterLenght; // 这句不能少。
                    }
                }
            }
            break;
        }
        return true;
    }

    private void setScale(float temp, int flag) {

        if (flag == ZOOM_BIGGER) {
            // setFrame(left , top, right,bottom)函数表示改变当前view的框架，也就是大小。
            this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
                    this.getTop() - (int) (temp * this.getHeight()), this.getRight() + (int) (temp * this.getWidth()),
                    this.getBottom() + (int) (temp * this.getHeight()));
        } else if (flag == ZOOM_SMALLER) {
            this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
                    this.getTop() + (int) (temp * this.getHeight()), this.getRight() - (int) (temp * this.getWidth()),
                    this.getBottom() - (int) (temp * this.getHeight()));
        }
    }

    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }
}
