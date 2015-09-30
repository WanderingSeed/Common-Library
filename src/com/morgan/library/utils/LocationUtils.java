package com.morgan.library.utils;

/**
 * 提供位置相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class LocationUtils {

	/**
	 * 获取两个位置之间的距离
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2) {
		if (lat1 == lat2 && lon1 == lon2) {
			return 0;
		}
		// haversine great circle distance approximation, returns meters
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		if (dist > 1) {
			return 0;
		}
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60; // 60 nautical miles per degree of separation
		dist = dist * 1852; // 1852 meters per nautical mile
		return dist / 1000;
	}

	/**
	 * 角度转弧度
	 * 
	 * @param deg
	 * @return
	 */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * 弧度转角度
	 * 
	 * @param rad
	 * @return
	 */
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
}
