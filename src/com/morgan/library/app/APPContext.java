package com.morgan.library.app;

import android.content.Context;

public class APPContext {
    private static Context sContext;

    static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
