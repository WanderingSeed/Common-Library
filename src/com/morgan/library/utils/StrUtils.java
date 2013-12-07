package com.morgan.library.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class StrUtils {

    public static String encodeUrl(String requestUrl, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(requestUrl);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            try {
                url.append(URLEncoder.encode(String.valueOf(params.get(name)), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url.toString().replace("?&", "?");
    }

    /**
     * 用参数替换URL里面用{}括起来的参数
     * 
     * @param url
     * @param parameter
     * @return
     */
    public static String Format(String url, Object... parameter) {
        String result = url;
        for (int i = 0; i < parameter.length; i++) {
            int startPoint = -1;
            int endPoint = -1;
            startPoint = result.indexOf('{');
            endPoint = result.indexOf('}');
            if (startPoint >= 0 && endPoint > startPoint) {
                result = result.substring(0, startPoint) + parameter[i].toString() + result.substring(endPoint + 1);
            }
        }
        return result;
    }
}
