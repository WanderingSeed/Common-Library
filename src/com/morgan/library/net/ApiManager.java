package com.morgan.library.net;

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
