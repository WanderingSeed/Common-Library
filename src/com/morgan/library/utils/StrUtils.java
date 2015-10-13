package com.morgan.library.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;

/**
 * 提供字符串相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class StrUtils {
	private final static Pattern EMAIL_PATTERN = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	/**
	 * 两位数字的格式化，为了公用写成静态变量
	 */
	private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();

	/**
	 * 编码GET请求
	 * 
	 * @param requestUrl
	 * @param params
	 * @return
	 */
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
				result = result.substring(0, startPoint) + parameter[i].toString()
						+ result.substring(endPoint + 1);
			}
		}
		return result;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input)) {
			return true;
		}
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断一个字符是否为中文
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	/**
	 * 判断中字符串内是否包含文汉字和符号
	 * 
	 * @param strName
	 * @return
	 */
	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 生成一个UUID
	 * 
	 * @return
	 */
	public static String generateId() {
		UUID uuid = UUID.randomUUID();
		String uniqueId = uuid.toString();
		return uniqueId;
	}

	/**
	 * MD5加密字符串
	 * 
	 * @param info
	 * @return
	 */
	public static String encryptToMD5(String info) {
		byte[] digesta = null;
		try {
			// 得到一个md5的消息摘要
			MessageDigest alga = MessageDigest.getInstance("MD5");
			// 添加要进行计算摘要的信息
			alga.update(info.getBytes());
			// 得到该摘要
			digesta = alga.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 将摘要转为字符串
		String rs = byte2hex(digesta);
		return rs;
	}

	/**
	 * byte转16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase(Locale.CHINESE);
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return EMAIL_PATTERN.matcher(email).matches();
	}

	public static final Formatter getTwoDigitFormatter() {
		return sTwoDigitFormatter;
	}

	/**
	 * Use a custom NumberPicker formatting callback to use two-digit minutes
	 * strings like "01". Keeping a static formatter etc. is the most efficient
	 * way to do this; it avoids creating temporary objects on every call to
	 * format().
	 */
	private static class TwoDigitFormatter implements NumberPicker.Formatter {
		final StringBuilder mBuilder = new StringBuilder();

		char mZeroDigit;
		java.util.Formatter mFmt;

		final Object[] mArgs = new Object[1];

		TwoDigitFormatter() {
			final Locale locale = Locale.getDefault();
			init(locale);
		}

		private void init(Locale locale) {
			mFmt = createFormatter(locale);
			mZeroDigit = getZeroDigit(locale);
		}

		public String format(int value) {
			final Locale currentLocale = Locale.getDefault();
			if (mZeroDigit != getZeroDigit(currentLocale)) {
				init(currentLocale);
			}
			mArgs[0] = value;
			mBuilder.delete(0, mBuilder.length());
			mFmt.format("%02d", mArgs);
			return mFmt.toString();
		}

		private static char getZeroDigit(Locale locale) {
			return new DecimalFormatSymbols(locale).getZeroDigit();
			// return LocaleData.get(locale).zeroDigit;
		}

		private java.util.Formatter createFormatter(Locale locale) {
			return new java.util.Formatter(mBuilder, locale);
		}
	}
}
