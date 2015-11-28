package com.morgan.library.widget.slidemenu;

import android.view.View;

import com.morgan.library.widget.slidemenu.ScrollDetectors.IScrollDetector;

/**
 * 滑动探测器生产工厂接口
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-11-09
 */
public interface IScrollDetectorFactory {
	/**
	 * 根据给定的View返回相应的滑动探测器
	 * 
	 * @param v
	 *            给定的View
	 * @return 相应的滑动探测器
	 */
	public IScrollDetector newScrollDetector(View v);
}
