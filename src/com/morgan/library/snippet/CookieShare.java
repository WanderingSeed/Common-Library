package com.morgan.library.snippet;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.morgan.library.app.APPContext;
import com.morgan.library.utils.Logger;

/**
 * 因为一些需要，在Webview中需要用到登陆时候的cookie，所以要做到cookie共享。
 * 
 * @author Morgan.Ji
 * 
 */
public class CookieShare {

	/**
	 * 使用用户名和密码登陆，获取得到的cookie并同步
	 * 
	 * @param name
	 * @param pass
	 * @return
	 */
	public boolean login(String name, String pass) {
		boolean result = false;
		try {
			String loginUrl = "http://10.1.1.157:8080/cas/login";
			BasicCookieStore bcsc = new BasicCookieStore();
			DefaultHttpClient client = new DefaultHttpClient();
			client.setCookieStore(bcsc);
			// get访问登陆页面
			HttpGet get = new HttpGet(loginUrl);
			HttpResponse response = client.execute(get);
			// 拿body
			String bodyAsString = EntityUtils.toString(response.getEntity());
			// 拿lt
			String lt = bodyAsString.substring(
					bodyAsString.indexOf("name=\"lt\"") + 17,
					bodyAsString.indexOf("name=\"lt\"") + 56);
			Logger.i("lt", lt);
			// 拿excution
			String execution = "e1s1";
			// post提交表单
			HttpPost post = new HttpPost(loginUrl);
			// 设置一系列的值
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", name));
			nvps.add(new BasicNameValuePair("password", pass));
			nvps.add(new BasicNameValuePair("lt", lt));
			nvps.add(new BasicNameValuePair("execution", execution));
			nvps.add(new BasicNameValuePair("_eventId", "submit"));
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

			response = client.execute(post);
			bodyAsString = EntityUtils.toString(response.getEntity());
			Logger.i("cookie", bcsc.getCookies().toString());
			// Logger.i("login response", bodyAsString);
			// 如果登陆成功，同步cookie
			if (bodyAsString.indexOf("id=\"msg\" class=\"success\"") > 0) {
				result = true;
				synCookies("http://10.1.1.175", bcsc.getCookies());
				// 访问其他的api
				get = new HttpGet(
						"http://10.1.1.175:8088/ioc/restapi/emergencyEvent/getEventByReportName");
				response = client.execute(get);
				bodyAsString = EntityUtils.toString(response.getEntity());
				Logger.i("get list response", bodyAsString);
			}
		} catch (Exception e) {
			Logger.i("error", e.getMessage());
		}
		return result;
	}

	/**
	 * 同步一下cookie
	 * 
	 * @param url
	 *            cookie所属的url,这个url必须要和domain相同(cookie内的domain字段可以去掉，不要带http://
	 *            );
	 * @param cookieList
	 *            cookie列表
	 */
	public void synCookies(String url, List<Cookie> cookieList) {
		String cookie = "";
		if (cookieList.isEmpty()) {
			return;
		}
		CookieSyncManager.createInstance(APPContext.getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		// 移除老的cookie（但是这个调用会报错,故暂时注释掉）
		// cookieManager.removeSessionCookie();
		for (int i = 0; i < cookieList.size(); i++) {
			cookie = cookieList.get(i).getName() + "="
					+ cookieList.get(i).getValue() + ";domain="
					+ cookieList.get(i).getDomain();
			cookieManager.setCookie(url, cookie);
		}
		CookieSyncManager.getInstance().sync();
	}
}
