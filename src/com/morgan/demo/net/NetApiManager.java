package com.morgan.demo.net;

/**
 * 管理网络请求的对象，方便在模拟的网络请求和真正的网络请求之间的切换。
 * 
 * @author Morgan.Ji
 * 
 */
public class NetApiManager {
	/**
	 * 当前应用状态，是否为模拟模式
	 */
	private static final boolean mMock = false;
	private static INetApi mApiClient;

	public static INetApi getApiClient() {
		if (null == mApiClient) {
			if (mMock) {
				mApiClient = new MockApiClient();
			} else {
				mApiClient = new NetApiClient();
			}
		}
		return mApiClient;
	}
}
