package com.morgan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 一个拦截向子View传递按压事件的LinearLayout
 * 
 * @author Morgan.Ji
 * 
 */
public class InterceptPressLayout extends LinearLayout {

    public InterceptPressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
    }
}
