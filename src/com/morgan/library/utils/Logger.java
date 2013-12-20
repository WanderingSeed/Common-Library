package com.morgan.library.utils;

import java.io.File;

import android.util.Log;

/**
 * 该类是为了开发android应用时更好的调试及记录应用产生的错误信息，在开发时设置debug为true,信息会在logcat中显示出来，安装到手机上时设置debug为false, 信息会记录在SD卡上的log文件中
 * 
 * @author Morgan.Ji
 * 
 */
public class Logger {

    private static final boolean DEBUG = true;
    private static final int mStoreLevel = Log.VERBOSE;
    private static final String LOG_FILE_PATH = SDCardUtils.getSDCardPath() + File.pathSeparator + "log.txt";
    private static final String DEFAULT_TAG = "default";

    /**
     * 在非debug模式下存储到文件的最低等级(共六个等级从verbose到assert(2到7))
     */

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(DEFAULT_TAG, msg);
        } else if (mStoreLevel >= Log.DEBUG) {
            FileUtils.writeFile(LOG_FILE_PATH, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        } else if (mStoreLevel >= Log.DEBUG) {
            FileUtils.writeFile(LOG_FILE_PATH, tag + " " + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(tag, msg, tr);
        } else if (mStoreLevel >= Log.DEBUG) {
            FileUtils.writeFile(LOG_FILE_PATH, tag + " " + msg + "/r/n" + tr.getMessage());
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(DEFAULT_TAG, msg);
        } else if (mStoreLevel >= Log.INFO) {
            FileUtils.writeFile(LOG_FILE_PATH, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        } else if (mStoreLevel >= Log.INFO) {
            FileUtils.writeFile(LOG_FILE_PATH, tag + " " + msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.i(tag, msg, tr);
        } else if (mStoreLevel >= Log.INFO) {
            FileUtils.writeFile(LOG_FILE_PATH, tag + " " + msg + "/r/n" + tr.getMessage());
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(DEFAULT_TAG, msg);
        } else if (mStoreLevel >= Log.ERROR) {
            FileUtils.writeFile(LOG_FILE_PATH, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        } else if (mStoreLevel >= Log.ERROR) {
            FileUtils.writeFile(LOG_FILE_PATH, tag + " " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(tag, msg, tr);
        } else if (mStoreLevel >= Log.ERROR) {
            FileUtils.writeFile(LOG_FILE_PATH, tag + " " + msg + "/r/n" + tr.getMessage());
        }
    }
}