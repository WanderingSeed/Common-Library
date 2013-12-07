package com.morgan.library.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 一个带有回弹动画的可缩放的图片组件。
 * 
 * @author jgc 2013-6-16
 */
public class ImageViewWidght extends ImageView {
    static final int NONE = 0;// 表示当前没有状态
    static final int DRAG = 1; // 表示当前处于移动状态
    static final int ZOOM = 2; // 表示当前处于缩放状态
    private int mode = NONE; // mode用于标示当前处于什么状态

    static final int BIGGER = 3; // 表示放大图片
    static final int SMALLER = 4; // 表示缩小图片

    private float beforeLenght; // 第一次触摸两点的距离
    private float afterLenght; // 移动后两点的距离
    private float scale = 0.04f; // 缩放因子

    private int screenW;// 图片的移动范围，也就是linearLayout的范围(一般是屏幕大小)
    private int screenH;

    private int start_x;// 开始触摸点
    private int start_y;
    private int stop_x;// 结束触摸点
    private int stop_y;
    private TranslateAnimation trans; // 回弹动画
    private boolean multi;// 多点触控是都都落到当前组件上(不知道为什么即使第二个点没有落在当前控件上也会触发当前控件的事件)
    private static final int MIN_LENGTH = 150;

    public ImageViewWidght(Context context, int w, int h)// 这里传进来的w，h就是图片的移动范围
    {
        super(context);
        this.setPadding(0, 0, 0, 0);
        this.setScaleType(ScaleType.CENTER);
        screenW = w;
        screenH = h;
    }

    @SuppressWarnings("deprecation")
    public ImageViewWidght(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setPadding(0, 0, 0, 0);
        this.setScaleType(ScaleType.CENTER);
        screenW = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        screenH = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
    }

    // 用来计算2个触摸点的距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {// MotionEvent.ACTION_MASK表示多点触控事件
        case MotionEvent.ACTION_DOWN:
            mode = DRAG;
            stop_x = (int) event.getRawX();// 表示相对于屏幕左上角为原点的坐标，getX是相对于父组件
            stop_y = (int) event.getRawY();// 同上
            start_x = stop_x - this.getLeft();// 用(int)event.getX()一样,表示相对于父组件的坐标，这里就是相对于自定义imageView左上角的坐标.建议用前者，如果不是全屏拖动，而是指定范围内，一样适用！
            start_y = stop_y - this.getTop();// 用(int)event.getY();一样,this.getTop()表示其顶部相对于父控件的距离
            multi = true;// 默认生效
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            // 防止指纹间的距离
            if (spacing(event) > 10f) {
                mode = ZOOM;
                beforeLenght = spacing(event);
                // 判断第二个点是否落在当前控件上，没有的话多点触控不生效
                if (event.getX(1) < getWidth() && event.getX(1) > 0 && event.getY(1) > 0 && event.getY(1) < getHeight()) {
                    multi = true;
                } else {
                    multi = false;
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            // 如果图片缩放到宽高任意一个小于150，那么自动放大，直到大于150，使能够多点触控
            while (getHeight() < MIN_LENGTH || getWidth() < MIN_LENGTH) {
                setScale(scale, BIGGER);
            }
            int disX = 0;
            int disY = 0;
            if (getHeight() <= screenH) {
                if (this.getTop() < 0) {
                    disY = getTop();
                    this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                } else if (this.getBottom() >= (screenH - 44)) {// 此处不知道为什么getBottom获得的坐标总是不对。所以要减44
                    disY = getBottom() - screenH + 44;
                    this.layout(this.getLeft(), screenH - getHeight() - 44, this.getRight(), screenH - 44);
                }
            } else {
                int Y1 = getTop();
                int Y2 = getHeight() - screenH + getTop();
                if (Y1 > 0) {
                    disY = Y1;
                    this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                } else if (Y2 < 0) {
                    disY = Y2;
                    this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH);
                }
            }
            if (getWidth() <= screenW) {
                if (this.getLeft() < 0) {
                    disX = getLeft();
                    this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
                } else if (this.getRight() > screenW) {
                    disX = getWidth() - screenW + getLeft();
                    this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
                }
            } else {
                int X1 = getLeft();
                int X2 = getWidth() - screenW + getLeft();
                if (X1 > 0) {
                    disX = X1;
                    this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
                } else if (X2 < 0) {
                    disX = X2;
                    this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
                }

            }
            // 根据disX和disY的偏移量采用移动动画回弹归位，动画时间为500毫秒。
            if (disX != 0 || disY != 0) {
                trans = new TranslateAnimation(disX, 0, disY, 0);
                trans.setDuration(500);
                this.startAnimation(trans);
            }
            mode = NONE;
            break;
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
                // 表示相对于屏幕左上角为原点的坐标
                stop_x = (int) event.getRawX();
                stop_y = (int) event.getRawY();
                // 执行拖动事件的时，不断变换自定义imageView的位置从而达到拖动效果
                this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y
                        - start_y + this.getHeight());
            } else if (mode == ZOOM && multi) {
                if (spacing(event) > 10f) {
                    afterLenght = spacing(event);
                    float gapLenght = afterLenght - beforeLenght;
                    if (gapLenght == 0) {
                        break;
                    }
                    // 图片宽度（也就是自定义imageView）必须大于70才可以缩放
                    else if (Math.abs(gapLenght) > 5f && getWidth() > 70) {
                        if (gapLenght > 0) {
                            this.setScale(scale, BIGGER);
                        } else {
                            this.setScale(scale, SMALLER);
                        }
                        beforeLenght = afterLenght; // 这句不能少。
                    }
                }
            }
            break;
        }
        return true;
    }

    private void setScale(float temp, int flag) {

        if (flag == BIGGER) {
            // setFrame(left , top, right,bottom)函数表示改变当前view的框架，也就是大小。
            this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
                    this.getTop() - (int) (temp * this.getHeight()), this.getRight() + (int) (temp * this.getWidth()),
                    this.getBottom() + (int) (temp * this.getHeight()));
        } else if (flag == SMALLER) {
            this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
                    this.getTop() + (int) (temp * this.getHeight()), this.getRight() - (int) (temp * this.getWidth()),
                    this.getBottom() - (int) (temp * this.getHeight()));
        }
    }

    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }
}
