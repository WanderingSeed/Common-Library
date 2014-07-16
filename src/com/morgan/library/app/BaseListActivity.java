package com.morgan.library.app;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * 应用程序ListActivity的基类，在创建时把自己添加到AppManager中管理，销毁时移除。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014-7-15
 */
public class BaseListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 新建时添加Activity到列表中
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// 结束时把Activity从列表中移除
		AppManager.getAppManager().removeActivity(this);
		super.onDestroy();
	}
}
