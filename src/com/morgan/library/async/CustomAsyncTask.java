package com.morgan.library.async;

import android.os.AsyncTask;

/**
 * 一个封装了{@link AsyncTask}并继承了{@link IDestroy}的抽象类，在继承并重写
 * {@link #onPostExecute(Object)} 时不要忘了调用super.onPostExecute(result)（一般是不需要重写的）。
 * 
 * @author Morgan.Ji
 */
public abstract class CustomAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> implements IDestroy {

	private IFeedback mFeedback;
	private boolean mDestoried = false;

	public CustomAsyncTask(IFeedback feedback) {
		this.mFeedback = feedback;
	}

	@Override
	public void destory() {
		if (cancelable() && !isCancelled()) {
			cancel(true);
		} else {
			mDestoried = true;
		}
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		if (!mDestoried) { // 只有没被销毁的任务才会执行回调
			mFeedback.onFeedback(getKey(), isSuccess(), result);
		}
	}

	public abstract String getKey();

	public abstract boolean isSuccess();
}
