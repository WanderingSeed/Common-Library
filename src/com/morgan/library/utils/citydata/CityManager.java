package com.morgan.library.utils.citydata;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.morgan.library.utils.FileUtils;
import com.morgan.library.utils.Logger;

public class CityManager {

	private static final String TAG = CityManager.class.getName();

	/**
	 * 获取所有地址
	 * 
	 * @param provinces
	 *            省份列表
	 * @param cities
	 *            城市列表
	 * @param areas
	 *            地区列表
	 */
	public static void getAllAddress(List<String> provinces, List<List<String>> cities,
			List<List<List<String>>> areas) {
		String json = "";
		try {
			json = FileUtils.readFile(getCityDataPath(), "utf-8");
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

	/**
	 * 获取所有省份
	 * 
	 * @return
	 */
	public static List<String> getAllProvinces() {
		List<String> provinces = new ArrayList<String>();
		String json = "";
		try {
			json = FileUtils.readFile(getCityDataPath());
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

	/**
	 * 获取所有城市
	 * 
	 * @return
	 */
	public static List<List<String>> getAllCities() {
		List<List<String>> cities = new ArrayList<List<String>>();
		String json = "";
		try {
			json = FileUtils.readFile(getCityDataPath());
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
	 * 获取所有区域
	 * 
	 * @return
	 */
	public static List<List<List<String>>> getAllAreas() {
		List<List<List<String>>> areas = new ArrayList<List<List<String>>>();
		String json = "";
		try {
			json = FileUtils.readFile(getCityDataPath());
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

	/**
	 * 获取城市数据的路径
	 * 
	 * @return 城市数据的路径
	 */
	private static String getCityDataPath() {
		String pkg = FileUtils.class.getName();
		int dot = pkg.lastIndexOf('.');
		if (dot != -1) {
			pkg = pkg.substring(0, dot).replace('.', '/');
		} else {
			pkg = "";
		}
		return pkg + "/areas.json";
	}
}
