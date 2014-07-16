package com.morgan.library.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 一个可以控制是否可以滑动的ViewPager
 * 
 * @author Morgan.Ji
 * 
 */
public class CustomViewPager extends ViewPager {

	private boolean scrollAble = true;

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (scrollAble) {
			super.scrollTo(x, y);
		}
	}

	public boolean isScrollAble() {
		return scrollAble;
	}

	public void setScrollAble(boolean scrollAble) {
		this.scrollAble = scrollAble;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (scrollAble) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}
	}

}
