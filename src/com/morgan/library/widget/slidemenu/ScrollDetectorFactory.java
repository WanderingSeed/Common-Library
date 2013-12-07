package com.morgan.library.widget.slidemenu;

import android.view.View;

import com.morgan.library.widget.slidemenu.ScrollDetectors.ScrollDetector;

public interface ScrollDetectorFactory {
    public ScrollDetector newScrollDetector(View v);
}
