package com.morgan.library.net;

/**
 * 管理网络请求的对象，方便在模拟的网络请求和真正的网络请求之间的切换。
 * 
 * @author Morgan.Ji
 * 
 */
public class ApiManager {
	private static final boolean mMock = false;
	private static IApiClient mApiClient;

	public static IApiClient getApiClient() {
		if (null == mApiClient) {
			if (mMock) {
				mApiClient = new MockApiClient();
			} else {
				mApiClient = new ApiClient();
			}
		}
		return mApiClient;
	}
}
