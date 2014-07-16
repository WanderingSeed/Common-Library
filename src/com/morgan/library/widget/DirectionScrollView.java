package com.morgan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 一个可以判断当前滑动方向并回调{@link #mOnDirectionChangeListener}内部方法的ScrollView
 * 
 * @author Morgan.Ji
 * 
 */
public class DirectionScrollView extends ScrollView {

	public static final int DIRECTION_NONE = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	private OnScrollDirectionChangeListener mOnDirectionChangeListener;
	private boolean mRecord = false;

	public DirectionScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DirectionScrollView(Context context) {
		super(context);
	}

	public DirectionScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (null != mOnDirectionChangeListener && mRecord) {
			if (t > oldt) {
				mOnDirectionChangeListener.onDirectionChange(DIRECTION_DOWN);
			} else if (t < oldt) {
				mOnDirectionChangeListener.onDirectionChange(DIRECTION_UP);
			} else {
				mOnDirectionChangeListener.onDirectionChange(DIRECTION_NONE);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mRecord = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mRecord = false;
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	public OnScrollDirectionChangeListener getOnScrollDirectionChangeListener() {
		return mOnDirectionChangeListener;
	}

	public void setOnScrollDirectionChangeListener(
			OnScrollDirectionChangeListener mScrollDirectionChangeListener) {
		this.mOnDirectionChangeListener = mScrollDirectionChangeListener;
	}

	public boolean isRecord() {
		return mRecord;
	}

	public void setRecord(boolean mRecord) {
		this.mRecord = mRecord;
	}

	public interface OnScrollDirectionChangeListener {
		public void onDirectionChange(int direction);
	}
}
