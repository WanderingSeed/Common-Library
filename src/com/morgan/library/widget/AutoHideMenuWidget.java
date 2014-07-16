package com.morgan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.morgan.library.R;
import com.morgan.library.widget.DirectionScrollView.OnScrollDirectionChangeListener;

/**
 * 一个根据当前ScrollView的滑动状态自动显示或者隐藏菜单，如果使用这个widget，必须在它内部添加id为ScrollView的
 * {@link DirectionScrollView} 和一个id为menu_container的GroupView.
 * 
 * @author Morgan.Ji
 * 
 */
public class AutoHideMenuWidget extends FrameLayout {

	private DirectionScrollView mScrollView;
	private TranslateAnimation mShowAnimation, mHideAnimation;
	private boolean mMoving, mShowAfterHide;
	private LinearLayout mMenuContainer;
	private OnTouchListener mOnTouchListenerDegelate;

	public AutoHideMenuWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initAnimatition();
	}

	private void initView() {
		mScrollView = (DirectionScrollView) findViewById(R.id.scrollview);
		mMenuContainer = (LinearLayout) findViewById(R.id.menu_container);
		if (null == mScrollView || null == mMenuContainer) {
			throw new RuntimeException(
					"must include scrollview and menu_container in this view");
		}
		mScrollView.setOnTouchListener(mOnTouchListener);
		mScrollView
				.setOnScrollDirectionChangeListener(mScrollDirectionChangeListener);
	}

	private void initAnimatition() {
		mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);
		mShowAnimation.setDuration(600);
		mShowAnimation.setInterpolator(new DecelerateInterpolator());
		mShowAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mMenuContainer.setVisibility(View.VISIBLE);
				mMoving = true;
				mShowAfterHide = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mMoving = false;
				mShowAfterHide = false;
			}
		});

		mHideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1);
		mHideAnimation.setDuration(400);
		mHideAnimation.setInterpolator(new AccelerateInterpolator(1.5f));
		mHideAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mMoving = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mMenuContainer.setVisibility(View.GONE);
				mMoving = false;
				if (mShowAfterHide) {
					toggleBottomBtn(true);
				}
			}
		});
	}

	private void toggleBottomBtn(boolean show) {
		if (mMoving) {
			if (show) {
				mShowAfterHide = true;
			}
			return;
		}
		if (show && !mMenuContainer.isShown()) {
			mMoving = true;
			mMenuContainer.postDelayed(new Runnable() {

				@Override
				public void run() {
					mMenuContainer.startAnimation(mShowAnimation);
				}
			}, 1000);
		} else if (!show && mMenuContainer.isShown()) {
			mMenuContainer.startAnimation(mHideAnimation);
		}
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean result = false;
			if (null != mOnTouchListenerDegelate) {
				result = mOnTouchListenerDegelate.onTouch(v, event);
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mScrollView.setRecord(true);
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mScrollView.setRecord(false);
				toggleBottomBtn(true);
				break;
			default:
				break;
			}
			return result;
		}
	};

	private OnScrollDirectionChangeListener mScrollDirectionChangeListener = new OnScrollDirectionChangeListener() {

		@Override
		public void onDirectionChange(int direction) {
			switch (direction) {
			case DirectionScrollView.DIRECTION_DOWN:
				toggleBottomBtn(false);
				break;
			default:
				break;
			}
		}
	};

	public void setOnTouchListener(OnTouchListener mOnTouchListener) {
		this.mOnTouchListenerDegelate = mOnTouchListener;
	}
}
