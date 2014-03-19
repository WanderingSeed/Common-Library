package com.morgan.library.app;

import android.content.Context;

/**
 * 为整个应用提供全局的Context，使用时要在Application类的构造器里面执行APPContext.init(this);
 * 
 * @author Morgan.Ji
 * 
 */
public class APPContext {
    private static Context sContext;

    static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
