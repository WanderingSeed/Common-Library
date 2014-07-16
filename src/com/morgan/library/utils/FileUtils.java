package com.morgan.library.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 提供文件相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class FileUtils {
	private static final String TAG = FileUtils.class.getName();
	private static BufferedReader br;

	/**
	 * 追加内容到文件中
	 * 
	 * @param filePath
	 *            文件的路径
	 * @param content
	 *            想要写入的信息
	 */
	public static void writeFile(String filePath, String content) {
		writeFile(filePath, content, true);
	}

	/**
	 * @param filePath
	 *            文件的路径
	 * @param content
	 *            想要写入的信息
	 * @param append
	 *            添加方式(true为追加,false为覆盖)
	 */
	public static void writeFile(String filePath, String content, boolean append) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, append);
			pw = new PrintWriter(fw);
			pw.write(content + "\r\n");
			pw.close();
			fw.close();
		} catch (Exception e) {
			// 因为logger里面调用了该方法，所以此处不能再循环调用Logger的log方法
			Log.e(TAG, e.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}

	/**
	 * 用于读取较小文本内的数据
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String readFile(String filePath) {
		BufferedInputStream buffered = null;
		String content = "";
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				return "";
			}
			buffered = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[buffered.available()];
			buffered.read(buffer);
			content = new String(buffer);
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		} finally {
			if (buffered != null) {
				try {
					buffered.close();
				} catch (IOException e) {
					Logger.e(TAG, e.getMessage());
				}
			}
		}
		return content;
	}

	public static boolean storeImage(Bitmap bitmap, String filePath) {
		boolean result = false;
		FileOutputStream b = null;
		try {
			b = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 85, b);
			result = true;
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				Logger.e(TAG, e.getMessage());
			}
		}
		return result;
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	public static String readFile(String filePath, String charset) {
		StringBuffer FileContent = new StringBuffer();
		try {
			FileInputStream inputStream = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(inputStream, charset);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				FileContent.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FileContent.toString();
	}

	public static void getAllAddress(List<String> provinces,
			List<List<String>> cities, List<List<List<String>>> areas) {
		String json = "";
		try {
			json = readFile(getCityDataPath(), "utf-8");
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
		try {
			JSONArray provincesObj = new JSONArray(json);
			for (int i = 0; i < provincesObj.length(); i++) {

				JSONObject provinceObj = provincesObj.getJSONObject(i);
				String province = provinceObj.getString("state");
				provinces.add(province);

				List<List<String>> provinceAreas = new ArrayList<List<String>>();
				List<String> provincecitys = new ArrayList<String>();

				JSONArray citiesObj = provinceObj.getJSONArray("cities");
				for (int j = 0; j < citiesObj.length(); j++) {
					JSONObject cityObj = citiesObj.getJSONObject(j);
					String city = cityObj.getString("city");
					provincecitys.add(city);

					List<String> cityAreas = new ArrayList<String>();
					JSONArray areasObj = cityObj.getJSONArray("areas");
					for (int k = 0; k < areasObj.length(); k++) {
						cityAreas.add(areasObj.getString(k));
					}

					provinceAreas.add(cityAreas);
				}
				cities.add(provincecitys);
				areas.add(provinceAreas);
			}
		} catch (JSONException e) {
			Logger.e(TAG, e.getMessage());
		}
	}

	public static List<String> getAllProvinces() {
		List<String> provinces = new ArrayList<String>();
		String json = "";
		try {
			json = readFile(getCityDataPath());
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
		try {
			JSONArray provincesObj = new JSONArray(json);
			for (int i = 0; i < provincesObj.length(); i++) {
				JSONObject provinceObj = provincesObj.getJSONObject(i);
				String province = provinceObj.getString("state");
				provinces.add(province);
			}
		} catch (JSONException e) {
			Logger.e(TAG, e.getMessage());
		}
		return provinces;

	}

	public static List<List<String>> getAllCities() {
		List<List<String>> cities = new ArrayList<List<String>>();
		String json = "";
		try {
			json = readFile(getCityDataPath());
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
		try {
			JSONArray provincesObj = new JSONArray(json);
			for (int i = 0; i < provincesObj.length(); i++) {
				JSONObject provinceObj = provincesObj.getJSONObject(i);
				List<String> citys = new ArrayList<String>();
				JSONArray citiesObj = provinceObj.getJSONArray("cities");
				for (int j = 0; j < citiesObj.length(); j++) {
					JSONObject cityObj = citiesObj.getJSONObject(j);
					String city = cityObj.getString("city");
					citys.add(city);
				}
				cities.add(citys);
			}
		} catch (JSONException e) {
			Logger.e(TAG, e.getMessage());
		}
		return cities;
	}

	/**
	 * 好像是不成功的，文件不存在，
	 * 
	 * @return
	 */
	public static List<List<List<String>>> getAllAreas() {
		List<List<List<String>>> areas = new ArrayList<List<List<String>>>();
		String json = "";
		try {
			json = readFile(getCityDataPath());
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
		}
		try {
			JSONArray provincesObj = new JSONArray(json);
			for (int i = 0; i < provincesObj.length(); i++) {
				JSONObject provinceObj = provincesObj.getJSONObject(i);
				List<List<String>> cities = new ArrayList<List<String>>();
				JSONArray citiesObj = provinceObj.getJSONArray("cities");
				for (int j = 0; j < citiesObj.length(); j++) {
					List<String> citys = new ArrayList<String>();
					JSONObject cityObj = citiesObj.getJSONObject(j);
					JSONArray areasObj = cityObj.getJSONArray("areas");
					for (int k = 0; k < areasObj.length(); k++) {
						citys.add(areasObj.getString(k));
					}
					cities.add(citys);
				}
				areas.add(cities);
			}
		} catch (JSONException e) {
			Logger.e(TAG, e.getMessage());
		}
		return areas;
	}

	private static String getCityDataPath() {
		String pkg = FileUtils.class.getName();
		int dot = pkg.lastIndexOf('.');
		if (dot != -1) {
			pkg = pkg.substring(0, dot).replace('.', '/');
		} else {
			pkg = "";
		}
		return pkg + "/" + "citydata/areas.json";
	}
}
