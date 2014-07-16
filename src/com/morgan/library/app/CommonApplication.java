package com.morgan.library.app;

import android.app.Application;

/**
 * 自定义全局Application。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014年7月9日
 */
public class CommonApplication extends Application {

	public CommonApplication() {
		super();
		APPContext.init(this);
	}

}
