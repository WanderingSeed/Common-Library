package com.morgan.library.utils;

import java.io.File;
import java.util.Date;

import android.util.Log;

/**
 * 该类是为了开发android应用时更好的调试及记录应用产生的错误信息，在开发时设置debug为true,信息会在logcat中显示出来，
 * 安装到手机上时设置debug为false, 信息会记录在SD卡上的log文件中
 * 
 * @author Morgan.Ji
 * 
 */
public class Logger {

	private static final boolean ENABLE_FILE_LOG = true;
	private static final int mStoreLevel = Log.VERBOSE;
	private static final String LOG_FILE_PATH = SDCardUtils.getSDCardPath()
			+ File.separator + "log.txt";
	private static final String DEFAULT_TAG = "default";

	/**
	 * 在非debug模式下存储到文件的最低等级(共六个等级从verbose到assert(2到7))
	 */

	public static void d(String msg) {
		Log.d(DEFAULT_TAG, msg);
		if (mStoreLevel <= Log.DEBUG && ENABLE_FILE_LOG) {
			writeFile(msg);
		}
	}

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
		if (mStoreLevel <= Log.DEBUG && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		Log.d(tag, msg, tr);
		if (mStoreLevel <= Log.DEBUG && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg + "/r/n" + tr.getMessage());
		}
	}

	public static void i(String msg) {
		Log.i(DEFAULT_TAG, msg);
		if (mStoreLevel <= Log.INFO && ENABLE_FILE_LOG) {
			writeFile(msg);
		}
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
		if (mStoreLevel <= Log.INFO && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		Log.i(tag, msg, tr);
		if (mStoreLevel <= Log.INFO && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg + "/r/n" + tr.getMessage());
		}
	}

	public static void e(String msg) {
		Log.e(DEFAULT_TAG, msg);
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(msg);
		}
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg);
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg);
		}
	}
	
	public static void e(String tag, Throwable tr) {
		Log.e(tag, tr.getMessage());
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(tag + " " + tr.getMessage());
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		Log.e(tag, msg, tr);
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg + "/r/n" + tr.getMessage());
		}
	}

	private static void writeFile(String msg) {
		FileUtils.appendFile(LOG_FILE_PATH, new Date().toString() + " " + msg);
	}
}