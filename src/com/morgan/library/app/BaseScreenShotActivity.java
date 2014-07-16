package com.morgan.library.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;

import com.morgan.library.widget.ScreenShotView;
import com.morgan.library.widget.ScreenShotView.OnScreenShotListener;

/**
 * 提供截屏功能的Activity的基类。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014年7月9日
 */
public class BaseScreenShotActivity extends BaseActivity {

	// 是否允许销毁
	private boolean mAllowDestroy = true;
	// 截屏的View
	private ScreenShotView mScreenShotView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 启动截屏模式。
	 */
	public void startShotScreen(OnScreenShotListener screenShotListener) {
		this.mAllowDestroy = false;
		if (mScreenShotView == null) {
			mScreenShotView = new ScreenShotView(BaseScreenShotActivity.this,
					screenShotListener);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			getWindow().addContentView(mScreenShotView, lp);
		}
		//this.mScreenShotView.
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果当前正在处于截屏的过程，则按back键只是退出截图功能。
		if (keyCode == KeyEvent.KEYCODE_BACK && !mAllowDestroy) {
			this.mScreenShotView.dismiss();
			mAllowDestroy = true;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
