package com.morgan.library.async;

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
