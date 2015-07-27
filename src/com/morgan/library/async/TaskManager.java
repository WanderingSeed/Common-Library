package com.morgan.library.async;

import java.util.HashMap;
import java.util.Map;

/**
 * 在应用运行过程中，Activity或Fragment已经被销毁而在它们内部启动的一些异步线程却没有被销毁，当这些线程回调时就会出现一些意想不到的错误，
 * 该类就是用来解决这类问题的。 你所需要做的是，让你的异步线程或那些会启动异步线程的widget实现{@link IDestroy}
 * 接口,然后把这些对象通过 {@link #addItem(String, DestroyAble)}
 * 方法加入管理，当然你需要在Activity或Fragment销毁的地方调用{@link #destory()}方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class TaskManager {

	private Map<String, IDestroy> mTasks = new HashMap<String, IDestroy>();

	/**
	 * @param key
	 *            用来标示可销毁对象的， 当已存在该key时，上一个可销毁对象就会被销毁
	 * @param item
	 *            可销毁对象的
	 */
	public void addItem(String key, IDestroy item) {
		IDestroy old = mTasks.get(key);
		if (null != old) {
			old.destory();
		}
		if (item != null) {
			mTasks.put(key, item);
		}
	}

	/**
	 * 调用所有可销毁对象的销毁方法
	 */
	public void destoryAll() {
		for (String key : mTasks.keySet()) {
			mTasks.get(key).destory();
		}
	}
}
