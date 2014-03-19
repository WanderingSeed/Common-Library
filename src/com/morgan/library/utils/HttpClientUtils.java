package com.morgan.library.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 提供网络发送相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class HttpClientUtils {
    private static final String TAG = HttpClientUtils.class.getName();
    private static final int CONNECT_ERROR_SLEEP_TIME = 1000;
    private static final int TIMEOUT_CONNECTION = 1000;
    private static final int TIMEOUT_SOCKET = 1000;
    private static final String HOST_URL = "";
    private static final String UTF_8 = "UTF-8";
    public final static String SERVER_CONNECT_ERROR = "{code:0,message:\"服务器连接失败\"}";
    private final static int RETRY_TIME = 3;
    private static String appUserAgent;

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集
        httpClient.getParams().setContentCharset(UTF_8);
        return httpClient;
    }

    private static GetMethod getHttpGet(String url, String userAgent) {
        GetMethod httpGet = new GetMethod(url);
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpGet.setRequestHeader("Host", HOST_URL);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        httpGet.setRequestHeader("User-Agent", userAgent);
        return httpGet;
    }

    private static PostMethod getHttpPost(String url, String userAgent) {
        PostMethod httpPost = new PostMethod(url);
        httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpPost.setRequestHeader("Host", HOST_URL);
        httpPost.setRequestHeader("Connection", "Keep-Alive");
        httpPost.setRequestHeader("User-Agent", userAgent);
        return httpPost;
    }

    private static String getUserAgent() {
        if (appUserAgent == null || appUserAgent == "") {
            StringBuilder ua = new StringBuilder("Morgan");
            ua.append('/' + AppUtils.getPackageInfo().versionName + '_' + AppUtils.getPackageInfo().versionCode);// App版本
            ua.append("/Android");// 手机系统平台
            ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
            ua.append("/" + android.os.Build.MODEL); // 手机型号
            appUserAgent = ua.toString();
        }
        return appUserAgent;
    }

    /**
     * get请求URL
     * 
     * @param url
     * @throws AppException
     */
    public static String get(String url) {
        Logger.d(TAG, "get request: " + url);
        String userAgent = getUserAgent();
        HttpClient httpClient = null;
        GetMethod httpGet = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, userAgent);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    responseBody = SERVER_CONNECT_ERROR;
                    break;
                } else {
                    responseBody = httpGet.getResponseBodyAsString();
                    break;
                }
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                responseBody = SERVER_CONNECT_ERROR;
            } catch (Exception e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                responseBody = SERVER_CONNECT_ERROR;
            } finally {
                // 释放连接
                if (null != httpGet) {
                    httpGet.releaseConnection();
                }
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        Logger.d(TAG, "get response: " + url + "/r/n" + responseBody);
        return responseBody;
    }

    public static String post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    /**
     * 公用post方法
     * 
     * @param url
     * @param params
     * @param files
     * @throws AppException
     */
    public static String post(String url, Map<String, Object> params, Map<String, File> files) {
        Logger.d(TAG, "post request: " + url + " params: " + params.toString());
        String userAgent = getUserAgent();
        HttpClient httpClient = null;
        PostMethod httpPost = null;

        // post表单参数处理
        int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
        Part[] parts = new Part[length];
        int i = 0;
        if (params != null) {
            for (String name : params.keySet()) {
                parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
            }
        }
        if (files != null) {
            for (String file : files.keySet()) {
                try {
                    parts[i++] = new FilePart(file, files.get(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(url, userAgent);
                httpPost.setRequestEntity(new MultipartRequestEntity(parts, httpPost.getParams()));
                int statusCode = httpClient.executeMethod(httpPost);
                if (statusCode != HttpStatus.SC_OK) {
                    responseBody = SERVER_CONNECT_ERROR;
                    break;
                } else {
                    responseBody = httpPost.getResponseBodyAsString();
                    break;
                }
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                responseBody = SERVER_CONNECT_ERROR;
            } catch (Exception e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                responseBody = SERVER_CONNECT_ERROR;
            } finally {
                // 释放连接
                if (null != httpPost) {
                    httpPost.releaseConnection();
                }
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        Logger.d(TAG, "post response: " + url + "/r/n" + responseBody);
        return responseBody;
    }

    /**
     * 公用post方法
     * 
     * @param url
     * @param params
     * @param files
     * @throws AppException
     */
    public static String postJson(String url, String json) {
        Logger.d(TAG, "post request: " + url + " params: " + json);
        String userAgent = getUserAgent();
        HttpClient httpClient = null;
        PostMethod httpPost = null;
        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(url, userAgent);
                StringRequestEntity requestEntity = new StringRequestEntity(json, "application/json", "UTF-8");
                httpPost.setRequestEntity(requestEntity);
                int statusCode = httpClient.executeMethod(httpPost);
                if (statusCode != HttpStatus.SC_OK) {
                    responseBody = SERVER_CONNECT_ERROR;
                    break;
                } else {
                    responseBody = httpPost.getResponseBodyAsString();
                    break;
                }
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(CONNECT_ERROR_SLEEP_TIME);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                responseBody = SERVER_CONNECT_ERROR;
            } catch (Exception e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                responseBody = SERVER_CONNECT_ERROR;
            } finally {
                // 释放连接
                if (null != httpPost) {
                    httpPost.releaseConnection();
                }
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        Logger.d(TAG, "post response: " + url + " /r/n " + responseBody);
        return responseBody;
    }
}
