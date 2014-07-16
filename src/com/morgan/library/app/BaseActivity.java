package com.morgan.library.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * 应用程序Activity的基类,以便于统一管理。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014年7月9日
 */
public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加新创建的Activity到列表中
		AppManager.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// 结束Activity从列表中移除
		AppManager.getInstance().removeActivity(this);
		super.onDestroy();
	}
}
