package com.morgan.library.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.morgan.library.net.JsonUtils;
import com.morgan.library.utils.HttpClientUtils;

/**
 * 对网络请求返回的json数据的初步解析，用于网络请求是否有错误分期，如果没错误，可以把解析后的VO存放在泛型字段里。
 * 
 * @author Morgan.Ji
 * 
 * @param <T>存放解析后的VO
 */
public class NetResult<T> {
	public static final int SUCCESS_CODE = 200;
	/**
	 * 网络返回状态。
	 */
	private int mStatusCode;
	/**
	 * 网络出错信息。
	 */
	private String mErrorMessage;
	/**
	 * 网络返回结果。
	 */
	private String mResponseMessage;

	private T mObject;

	public NetResult(String vo) {
		if (HttpClientUtils.SERVER_CONNECT_ERROR.equals(vo)) {
			try {
				JSONObject jsonObject = new JSONObject(vo);
				this.mStatusCode = jsonObject.getInt("code");
				this.mErrorMessage = jsonObject.optString("message");
			} catch (JSONException e) {
				this.mStatusCode = JsonUtils.JSON_PARSE_ERROR_CODE;
				this.mErrorMessage = JsonUtils.JSON_PARSE_ERROR_MESSAGE;
			}
		} else {
			this.mResponseMessage = vo;
		}
	}

	public boolean isSuccess() {
		return mStatusCode == SUCCESS_CODE;
	}

	public void setStatusCode(int statusCode) {
		this.mStatusCode = statusCode;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.mErrorMessage = errorMessage;
	}

	public String getResponseMessage() {
		return mResponseMessage;
	}

	public T getObject() {
		return mObject;
	}

	public void setObject(T mObject) {
		this.mObject = mObject;
	}
}
