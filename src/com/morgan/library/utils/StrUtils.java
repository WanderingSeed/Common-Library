package com.morgan.library.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class StrUtils {

    public static String encodeUrl(String requestUrl, Map<String, Object> params)
    {
        StringBuilder url = new StringBuilder(requestUrl);
        if (url.indexOf("?") < 0) url.append('?');

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
}
