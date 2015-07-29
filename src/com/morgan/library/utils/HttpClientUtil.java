package com.morgan.library.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * 使用android内置的HttpClient，发送网络请求。(文件上传需要httpcore-4.3.2.jar，httpmime-4.3.3.jar)
 * 
 * @author Morgan.Ji
 * 
 */
public class HttpClientUtil {

	private static final String TAG = HttpClientUtil.class.getName();
	/**
	 * 请求失败时和下次请求间隔时间
	 */
	private static final int CONNECT_ERROR_SLEEP_TIME = 1000;
	public final static String SERVER_CONNECT_ERROR = "{code:0,message:\"connect server fail\"}";
	/**
	 * 网络请求失败重试次数
	 */
	private final static int RETRY_TIME = 3;

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	public static String get(String url, Map<String, Object> params) {
		Logger.d(TAG, "get request: " + url);
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String responseBody = "";
		int retryTime = 0;
		do {
			try {
				httpClient = new DefaultHttpClient();
				httpGet = new HttpGet(url);
				HttpResponse response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					responseBody = SERVER_CONNECT_ERROR;
					break;
				} else {
					responseBody = EntityUtils.toString(response.getEntity());
					break;
				}
			} catch (Exception e) {
				retryTime++;
				if (retryTime < RETRY_TIME) {
					try {
						Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				responseBody = SERVER_CONNECT_ERROR;
			} finally {
				httpClient = null;
			}
		} while (retryTime < RETRY_TIME);
		Logger.d(TAG, "get response: " + url + "/r/n" + responseBody);
		return responseBody;
	}

	/**
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, Object> params) {
		Logger.d(TAG, "post request: " + url);
		HttpClient httpClient = null;
		HttpPost post = null;
		String responseBody = "";
		int retryTime = 0;
		do {
			try {
				httpClient = new DefaultHttpClient();
				post = new HttpPost();
				if (null != params) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for (String name : params.keySet()) {
						nvps.add(new BasicNameValuePair(name, String.valueOf(params
								.get(name))));
					}
					post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
				}
				HttpResponse response = httpClient.execute(post);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					responseBody = SERVER_CONNECT_ERROR;
					break;
				} else {
					responseBody = EntityUtils.toString(response.getEntity());
					break;
				}
			} catch (Exception e) {
				retryTime++;
				if (retryTime < RETRY_TIME) {
					try {
						Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				responseBody = SERVER_CONNECT_ERROR;
			} finally {
				httpClient = null;
			}
		} while (retryTime < RETRY_TIME);
		Logger.d(TAG, "post response: " + url + "/r/n" + responseBody);
		return responseBody;
	}

	/**
	 * 公用post方法，可上传文件
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	public static String post(String url, Map<String, Object> params,
			Map<String, File> files) {
		Logger.d(TAG, "post request: " + url + " params: " + params.toString());
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		String responseBody = "";
		int retryTime = 0;
		do {
			try {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.setCharset(Charset.forName(HTTP.UTF_8));
				ContentType TEXT_PLAIN = ContentType.create("text/plain",
						Charset.forName(HTTP.UTF_8));
				if (null != params) {
					for (String name : params.keySet()) {
						builder.addTextBody(name, String.valueOf(params.get(name)),
								TEXT_PLAIN);
					}
				}
				if (null != files) {
					for (String name : files.keySet()) {
						builder.addBinaryBody(name, files.get(name));
					}
				}
				HttpEntity reqEntity = builder.build();
				httpPost.setEntity(reqEntity);
				httpPost.getParams().setParameter("charset", HTTP.UTF_8);
				HttpResponse response = httpClient.execute(httpPost);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					responseBody = EntityUtils.toString(response.getEntity());
					break;
				} else {
					responseBody = SERVER_CONNECT_ERROR;
					break;
				}
			} catch (Exception e) {
				retryTime++;
				if (retryTime < RETRY_TIME) {
					try {
						Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				responseBody = SERVER_CONNECT_ERROR;
			} finally {
				httpClient = null;
			}
		} while (retryTime < RETRY_TIME);
		Logger.d(TAG, "post response: " + url + " /r/n " + responseBody);
		return responseBody;
	}

}
