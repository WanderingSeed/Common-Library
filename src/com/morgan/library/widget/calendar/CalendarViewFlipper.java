package com.morgan.library.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class CalendarViewFlipper extends ViewFlipper {
    public CalendarViewFlipper(Context context) {
        super(context);
    }

    public CalendarViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}
