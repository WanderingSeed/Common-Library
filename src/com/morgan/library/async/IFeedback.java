package com.morgan.library.async;

import android.os.AsyncTask;

/**
 * Activity内经常使用继承于AsyncTask的线程，如果有的这些类能够公用则需要把它提取出来，该接口就是用来存放分离出来的
 * {@link AsyncTask}中的{@link AsyncTask#onPostExecute(Object)}方法的。
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-7-22
 */
public interface IFeedback {

	/**
	 * @param key
	 *            the feedback key
	 * @param isSuccess
	 *            is success
	 * @param result
	 *            return object
	 * @return
	 */
	public boolean onFeedback(String key, boolean isSuccess, Object result);
}
