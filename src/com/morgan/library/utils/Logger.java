package com.morgan.library.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * 该类是为了开发android应用时更好的调试及记录应用产生的错误信息，在开发时设置debug为true,信息会在logcat中显示出来，
 * 安装到手机上时设置debug为false, 信息会记录在SD卡上的log文件中
 * 
 * @author Morgan.Ji
 * 
 */
public class Logger {

	/**
	 * 是否在调试，不调试都没必要输出logcat
	 */
	private static final boolean DEBUG = true;
	/**
	 * 是否将错误记录到文件
	 */
	private static final boolean ENABLE_FILE_LOG = true;

	/**
	 * 错误日志文件大小最大值为5M
	 */
	private static final int LOG_FILE_MAX_SIZE = 1024 * 1024 * 5;
	/**
	 * 如果记录到文件，则记录的最低程度
	 */
	private static final int mStoreLevel = Log.VERBOSE;
	private static final String LOG_FILE_DIR = SDCardUtils.getSDCardPath()
			+ File.separator + "log";
	private static final String DEFAULT_TAG = "default";

	private static final FileModifiedComparator mComparator = new FileModifiedComparator();

	/**
	 * 在非debug模式下存储到文件的最低等级(共六个等级从verbose到assert(2到7))
	 */
	public static void d(String msg) {
		if (DEBUG) {
			Log.d(DEFAULT_TAG, msg);
		}
		if (mStoreLevel <= Log.DEBUG && ENABLE_FILE_LOG) {
			writeFile(msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {

			Log.d(tag, msg);
		}
		if (mStoreLevel <= Log.DEBUG && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.d(tag, msg, tr);
		}
		if (mStoreLevel <= Log.DEBUG && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg + "/r/n" + tr.getMessage());
		}
	}

	public static void i(String msg) {
		if (DEBUG) {
			Log.i(DEFAULT_TAG, msg);
		}
		if (mStoreLevel <= Log.INFO && ENABLE_FILE_LOG) {
			writeFile(msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
		if (mStoreLevel <= Log.INFO && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.i(tag, msg, tr);
		}
		if (mStoreLevel <= Log.INFO && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg + "/r/n" + tr.getMessage());
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			Log.e(DEFAULT_TAG, msg);
		}
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg);
		}
	}

	public static void e(String tag, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, tr.getMessage());
		}
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(tag + " " + tr.getMessage());
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, msg, tr);
		}
		if (mStoreLevel <= Log.ERROR && ENABLE_FILE_LOG) {
			writeFile(tag + " " + msg + "/r/n" + tr.getMessage());
		}
	}

	private static void writeFile(String msg) {
		// 是否有SD
		if (SDCardUtils.isSDCardBusy()) {
			try {
				// 获取编号最大的文件
				File dir = new File(LOG_FILE_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = null;
				if (dir.isDirectory()) {
					File[] list = dir.listFiles();
					if (list.length > 0) {
						file = Collections.max(Arrays.asList(list), mComparator);
					} else {
						file = new File(dir, new SimpleDateFormat("yy-MM-dd HH-mm",
								Locale.CHINESE).format(new Date()) + ".txt");
					}
					// 打开一个随机访问文件流，按读写方式
					RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
					// 文件长度，字节数
					long fileLength = randomFile.length();
					if (fileLength > LOG_FILE_MAX_SIZE) {
						file = new File(dir, new SimpleDateFormat("yyyy-MM-dd HH-mm",
								Locale.CHINESE).format(new Date()) + ".txt");
						try {
							// 先关闭前一个文件流
							randomFile.close();
						} catch (Exception e) {
						}
						randomFile = new RandomAccessFile(file, "rw");
						fileLength = 0;
					}
					// 将写文件指针移到文件尾。
					randomFile.seek(fileLength);
					// 写入应用程序完整标识和日期
					randomFile.writeBytes("log time: " + DateUtils.getCurrentTime()
							+ "/r/n");
					// 写入日志
					randomFile.writeBytes(msg + "/r/n");
					randomFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}