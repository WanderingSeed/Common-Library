package com.morgan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import com.morgan.library.R;

public class SlidePageWidget extends LinearLayout {
    private float mStartY, mTempY;
    private Scroller mScroller;
    private boolean mMovable, mGone;
    private ImageView mImageView;
    private Context mContext;
    private OnSlidePageGoneListener mSlidePageGoneListener;
    private VelocityTracker mVelocityTracker;
    private float mMaximumVelocity;
    private int mMinimumVelocity;

    public SlidePageWidget(Context context) {
        this(context, null);
    }

    public SlidePageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidePage);
        mScroller = new Scroller(context, new BounceInterpolator());
        mImageView = new ImageView(context);
        int imageResource = ta.getResourceId(R.styleable.SlidePage_image, -1);
        if (imageResource != -1) {
            mImageView.setImageResource(imageResource);
        } else {
            this.setVisibility(View.GONE);
        }
        mImageView.setScaleType(ScaleType.FIT_XY);
        mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(mImageView);
        ta.recycle();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = 2000;
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker(event);
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mStartY = event.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            mTempY = event.getY();
            if (mTempY < mStartY) {
                mMovable = true;
            } else {
                mStartY = mTempY;
            }
            if (mMovable) {
                scrollTo(0, (int) (mStartY - mTempY));
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mMovable) {
                mMovable = false;
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if ((Math.abs(initialVelocity) > mMinimumVelocity) && getChildCount() > 0) {
                    mScroller = new Scroller(mContext, new DecelerateInterpolator());
                    mScroller.startScroll(0, (int) (mStartY - mTempY) - getHeight(), 0, getHeight()
                            - (int) (mStartY - mTempY), 1000);
                    invalidate();
                    mGone = true;
                } else {
                    mTempY = event.getY();
                    if ((mStartY - mTempY) > getHeight() * 0.4) {
                        mScroller = new Scroller(mContext, new DecelerateInterpolator());
                        mScroller.startScroll(0, (int) (mStartY - mTempY) - getHeight(), 0, getHeight()
                                - (int) (mStartY - mTempY), 1000);
                        invalidate();
                        mGone = true;
                    } else {
                        mScroller.startScroll(0, (int) (mTempY - mStartY), 0, (int) (mStartY - mTempY), 1000);
                        invalidate();
                    }
                }
            }
            releaseVelocityTracker();
            break;
        default:
            break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                int oldY = getScrollY();
                int y = mScroller.getCurrY();
                if (oldY != y && !mGone) {
                    scrollTo(0, -y);
                } else if (mGone) {
                    scrollTo(0, getHeight() + y);
                }
                invalidate();
                return;
            }
        } else {
            if (mGone) {
                this.setVisibility(View.GONE);
                if (null != mSlidePageGoneListener) {
                    mSlidePageGoneListener.onSlidePageGone();
                }
            }
        }
    }

    public interface OnSlidePageGoneListener {
        public void onSlidePageGone();
    }

    public OnSlidePageGoneListener getSlidePageGoneListener() {
        return mSlidePageGoneListener;
    }

    public void setOnSlidePageGoneListener(OnSlidePageGoneListener mSlidePageGoneListener) {
        this.mSlidePageGoneListener = mSlidePageGoneListener;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}