package com.morgan.library.app;

import android.app.Application;

public class CommonApplication extends Application {

    private static CommonApplication mInstance;

    private CommonApplication() {
        super();
        mInstance = this;
        APPContext.init(this);
    }

    public static Application getInstance()
    {
        if (null == mInstance) {
            mInstance = new CommonApplication();
        }
        return mInstance;
    }
}
