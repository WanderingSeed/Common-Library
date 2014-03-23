package com.morgan.library.app;

import android.app.Application;

/**
 * 自定义全局Application
 * 
 * @author Morgan.Ji
 * 
 */
public class CommonApplication extends Application {

    public CommonApplication() {
        super();
        APPContext.init(this);
    }
}
