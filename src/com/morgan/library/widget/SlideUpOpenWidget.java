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

/**
 * 一个向上滑动滑动到一定程度隐藏的View,否侧回落下来，一般用于包含一个ImageView。
 * 
 * @author Morgan.Ji
 * 
 */
public class SlideUpOpenWidget extends LinearLayout {

	private static final int DEFAULT_MINIMUM_VELOCITY = 2000;
	private static final int DEFAULT_ANIMATION_TIME = 800;
	private float mUpPercent = 0.5f;
	private float mStartY, mTempY;
	private Scroller mScroller;
	private boolean mMovable, mGone;
	private ImageView mImageView;
	private Context mContext;
	private OnSlidePageGoneListener mSlidePageGoneListener;
	private VelocityTracker mVelocityTracker;
	private float mMaximumVelocity;
	private int mMinimumVelocity;

	public SlideUpOpenWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SlideUpOpenWidget);
		mScroller = new Scroller(context, new BounceInterpolator());
		mImageView = new ImageView(context);
		int imageResource = ta.getResourceId(
				R.styleable.SlideUpOpenWidget_coverImage, -1);
		if (imageResource != -1) {
			mImageView.setImageResource(imageResource);
		} else {
			this.setVisibility(View.GONE);
		}
		mImageView.setScaleType(ScaleType.FIT_XY);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		this.addView(mImageView);
		ta.recycle();
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mMinimumVelocity = DEFAULT_MINIMUM_VELOCITY;
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
				if ((Math.abs(initialVelocity) > mMinimumVelocity)
						&& getChildCount() > 0) {
					mScroller = new Scroller(mContext,
							new DecelerateInterpolator());
					mScroller.startScroll(0, (int) (mStartY - mTempY), 0,
							(int) (getHeight() - (mStartY - mTempY)),
							DEFAULT_ANIMATION_TIME);
					invalidate();
					mGone = true;
				} else {
					mTempY = event.getY();
					if ((mStartY - mTempY) > getHeight() * mUpPercent) {
						mScroller = new Scroller(mContext,
								new DecelerateInterpolator());
						mScroller.startScroll(0, (int) (mStartY - mTempY), 0,
								(int) (getHeight() - (mStartY - mTempY)),
								DEFAULT_ANIMATION_TIME);
						invalidate();
						mGone = true;
					} else {
						mScroller.startScroll(0, (int) (mStartY - mTempY), 0,
								(int) (mTempY - mStartY),
								DEFAULT_ANIMATION_TIME);
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
				if (oldY != y) {
					scrollTo(0, y);
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

	public void setOnSlidePageGoneListener(
			OnSlidePageGoneListener mSlidePageGoneListener) {
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