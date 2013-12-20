package com.morgan.library.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CalendarScrollView extends ScrollView {

    private boolean mIsScrollY;
    private boolean mIsDectedScroll;
    private float mInitialMotionX;
    private float mInitialMotionY;

    public CalendarScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            final float x = ev.getX();
            final float y = ev.getY();
            mInitialMotionX = x;
            mInitialMotionY = y;
            mIsDectedScroll = false;
            break;
        case MotionEvent.ACTION_MOVE:
            if (!mIsDectedScroll && (ev.getY() != mInitialMotionY || ev.getX() != mInitialMotionX)) {
                mIsScrollY = Math.abs(ev.getY() - mInitialMotionY) > Math.abs(ev.getX() - mInitialMotionX);
                mIsDectedScroll = true;
            }
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mIsDectedScroll = false;
            mIsScrollY = false;
            break;
        }

        if (mIsDectedScroll && !mIsScrollY) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
