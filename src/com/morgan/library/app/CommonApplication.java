package com.morgan.library.app;

import android.app.Application;

public class CommonApplication extends Application {

    public CommonApplication() {
        super();
        APPContext.init(this);
    }
}
