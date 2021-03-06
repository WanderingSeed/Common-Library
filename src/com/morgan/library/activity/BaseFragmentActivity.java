package com.morgan.library.activity;

import com.morgan.library.app.ActicityManager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 应用程序FragmentActivity的基类，在创建时把自己添加到AppManager中管理，销毁时移除。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014-7-15
 */
public class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 新建时添加Activity到列表中
		ActicityManager.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// 结束时把Activity从列表中移除
		ActicityManager.getInstance().removeActivity(this);
		super.onDestroy();
	}
}
