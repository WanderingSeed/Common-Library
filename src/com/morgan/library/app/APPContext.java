package com.morgan.library.app;

import android.content.Context;

/**
 * 为整个应用提供全局的Context，使用时要在Application类的构造器里面执行APPContext.init(this)。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014年7月9日
 */
public class APPContext {

	private static Context mContext;

	static void init(Context context) {
		mContext = context;
	}

	/**
	 * 获取可使用的Context，只要应用存在，返回值就不会为null。
	 * 
	 * @return Context
	 */
	public static Context getContext() {
		return mContext;
	}
}
