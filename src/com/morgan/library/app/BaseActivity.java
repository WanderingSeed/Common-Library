package com.morgan.library.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * 应用程序Activity的基类
 * 
 * @author Morgan.Ji
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到列表
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        // 结束Activity列表中移除
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }
}
