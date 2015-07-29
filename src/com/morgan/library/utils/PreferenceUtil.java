package com.morgan.library.utils;

import com.morgan.library.app.APPContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 为了更方便的使用Preference存储和获取数据
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-7-29
 */
public class PreferenceUtil {

	private static PreferenceUtil mInstance;

	private SharedPreferences mPreference;

	/**
	 * 私有构造函数保证单例
	 * 
	 * @param context
	 */
	private PreferenceUtil(Context context) {
		mPreference = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 获取单例
	 * 
	 * @param context
	 * @return SharedPreferences 实例
	 */
	public static PreferenceUtil getInstance() {
		if (mInstance == null) {
			mInstance = new PreferenceUtil(APPContext.getContext());
		}
		return mInstance;
	}

	/**
	 * 读取字符串值
	 * 
	 * @param key
	 *            键
	 * @param defValue
	 *            默认值
	 * @return
	 */
	public String getString(String key, String defValue) {
		String value = defValue;
		if (!TextUtils.isEmpty(key)) {
			value = mPreference.getString(key, defValue);
		}
		return value;
	}

	/**
	 * 读取布尔值
	 * 
	 * @param key
	 *            键
	 * @param defValue
	 *            默认值
	 * @return
	 */
	public boolean getBoolean(String key, boolean defValue) {
		boolean value = defValue;
		if (!TextUtils.isEmpty(key)) {
			value = mPreference.getBoolean(key, defValue);
		}
		return value;
	}

	/**
	 * 读取整数值
	 * 
	 * @param key
	 *            键
	 * @param defValue
	 *            默认值
	 * @return
	 */
	public int getInt(String key, int defValue) {
		int value = defValue;
		if (!TextUtils.isEmpty(key)) {
			value = mPreference.getInt(key, defValue);
		}
		return value;
	}

	/**
	 * 读取浮点型数据
	 * 
	 * @param key
	 *            标识
	 * @param defValue
	 *            默认值
	 * @return
	 */
	public float getFloat(String key, float defValue) {
		float value = defValue;
		if (!TextUtils.isEmpty(key)) {
			value = mPreference.getFloat(key, defValue);
		}
		return value;
	}

	/**
	 * 保存字符串数据
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            输入值
	 */
	public void putString(String key, String value) {
		if (!TextUtils.isEmpty(key)) {
			mPreference.edit().putString(key, value).commit();
		}
	}

	/**
	 * 保存布尔值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            输入值
	 */
	public void putBoolean(String key, boolean value) {
		mPreference.edit().putBoolean(key, value).commit();
	}

	/**
	 * 保存整数
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            输入值
	 */
	public void putInt(String key, int value) {
		mPreference.edit().putInt(key, value).commit();
	}

	/**
	 * 保存浮点数
	 * 
	 * @param key
	 *            标识
	 * @param value
	 *            输入值
	 */
	public void putFloat(String key, float value) {
		mPreference.edit().putFloat(key, value).commit();
	}

	/**
	 * 检验SharedPreferences是否有这个键
	 * 
	 * @param key
	 *            键
	 * @return 是否包含
	 */
	public boolean contains(String key) {
		return mPreference.contains(key);
	}

	/**
	 * 删除这个键
	 * 
	 * @param key
	 *            键
	 * @return 是否成功删除
	 */
	public boolean remove(String key) {
		return mPreference.edit().remove(key).commit();
	}
}
