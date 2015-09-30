package com.morgan.library.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * 日历每个月控件之间的滑动控件，只做稍微更改
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-7-31
 */
public class CalendarViewFlipper extends ViewFlipper {
	public CalendarViewFlipper(Context context) {
		super(context);
	}

	public CalendarViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onDetachedFromWindow() {
		// 防止进行横竖屏切换操作时，要在重载Activity的onDetachedFromWindow()中调用
		try {
			super.onDetachedFromWindow();
		} catch (IllegalArgumentException e) {
			stopFlipping();
		}
	}
}
