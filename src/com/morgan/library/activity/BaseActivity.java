package com.morgan.library.activity;

import com.morgan.library.app.ActicityManager;

import android.app.Activity;
import android.os.Bundle;

/**
 * 应用程序Activity的基类,以便于统一管理，在创建时把自己添加到AppManager中管理，销毁时移除。
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
		ActicityManager.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// 结束Activity从列表中移除
		ActicityManager.getInstance().removeActivity(this);
		super.onDestroy();
	}
}
