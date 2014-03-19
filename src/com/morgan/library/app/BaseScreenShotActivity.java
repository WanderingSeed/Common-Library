package com.morgan.library.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * 提供截屏功能的Activity的基类
 * 
 * @author Morgan.Ji
 */
public class BaseScreenShotActivity extends BaseActivity {

    // 是否允许销毁
    private boolean mAllowDestroy = true;

    private View mScreenShotView;

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

    public void setAllowDestroy(boolean allowDestroy) {
        this.mAllowDestroy = allowDestroy;
    }

    public void setAllowDestroy(boolean allowDestroy, View view) {
        this.mAllowDestroy = allowDestroy;
        this.mScreenShotView = view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mScreenShotView != null) {
            mScreenShotView.onKeyDown(keyCode, event);
            if (!mAllowDestroy) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
