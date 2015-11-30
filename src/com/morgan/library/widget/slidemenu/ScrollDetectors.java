package com.morgan.library.widget.slidemenu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;

/**
 * 滑动探测器，根据控件类型使用相应的滑动探测器来探测该控件是否能够继续往某个方向滑动
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-11-09
 */
public class ScrollDetectors {
	/**
	 * 以享元模式分享已构造的探测器对象
	 */
	private static final WeakHashMap<Class<?>, IScrollDetector> IMPLES = new WeakHashMap<Class<?>, IScrollDetector>();
	/**
	 * 滑动探测器工厂
	 */
	private static IScrollDetectorFactory mFactory;

	/**
	 * 探测一个View是否能继续横向滑动
	 * 
	 * @param v
	 *            探测的View
	 * @param direction
	 *            手指滑动方向left: direction<0, right: direction>0
	 * @return
	 */
	public static boolean canScrollHorizontal(View v, int direction) {
		IScrollDetector imples = getImplements(v);
		if (null == imples) {
			return false;
		}
		return imples.canScrollHorizontal(v, direction);
	}

	/**
	 * 探测一个View是否能继续纵向滑动
	 * 
	 * @param v
	 *            探测的View
	 * @param direction
	 *            手指滑动方向bottom: direction<0, top: direction>0
	 * @return
	 */
	public static boolean canScrollVertical(View v, int direction) {
		IScrollDetector imples = getImplements(v);
		if (null == imples) {
			return false;
		}
		return imples.canScrollVertical(v, direction);
	}

	/**
	 * 获取相应控件的滑动探测器
	 * 
	 * @param v
	 *            要探测的View
	 * @return
	 */
	private static IScrollDetector getImplements(View v) {
		Class<?> clazz = v.getClass();
		IScrollDetector imple = IMPLES.get(clazz);

		if (null != imple) {
			return imple;
		}

		if (v instanceof ViewPager) {
			imple = new ViewPagerScrollDetector();
		} else if (v instanceof HorizontalScrollView) {
			imple = new HorizontalScrollViewScrollDetector();
		} else if (v instanceof WebView) {
			imple = new WebViewScrollDetector();
		} else if (null != mFactory) {
			imple = mFactory.newScrollDetector(v);
		} else {
			return null;
		}

		IMPLES.put(clazz, imple);
		return imple;
	}

	/**
	 * ViewPager滑动探测器
	 * 
	 * @author JiGuoChao
	 * 
	 * @version 1.0
	 * 
	 * @date 2015-11-09
	 */
	private static class ViewPagerScrollDetector implements IScrollDetector {

		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			ViewPager viewPager = (ViewPager) v;
			PagerAdapter pagerAdapter = viewPager.getAdapter();
			if (null == pagerAdapter) {
				return false;
			}

			final int currentItem = viewPager.getCurrentItem();
			return (direction < 0 && currentItem < pagerAdapter.getCount() - 1)
					|| (direction > 0 && currentItem > 0);
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			return false;
		}

	}

	/**
	 * WebView滑动探测器
	 * 
	 * @author JiGuoChao
	 * 
	 * @version 1.0
	 * 
	 * @date 2015-11-09
	 */
	private static class WebViewScrollDetector implements IScrollDetector {

		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			try {
				// 要调用的方法是protected，需要反射机制
				Method computeHorizontalScrollOffsetMethod = WebView.class
						.getDeclaredMethod("computeHorizontalScrollOffset");
				Method computeHorizontalScrollRangeMethod = WebView.class
						.getDeclaredMethod("computeHorizontalScrollRange");
				computeHorizontalScrollOffsetMethod.setAccessible(true);
				computeHorizontalScrollRangeMethod.setAccessible(true);

				final int horizontalScrollOffset = (Integer) computeHorizontalScrollOffsetMethod
						.invoke(v);
				final int horizontalScrollRange = (Integer) computeHorizontalScrollRangeMethod
						.invoke(v);

				return (direction > 0 && v.getScrollX() > 0)
						|| (direction < 0 && horizontalScrollOffset < horizontalScrollRange
								- v.getWidth());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			return false;
		}

	}

	/**
	 * HorizontalScrollView滑动探测器
	 * 
	 * @author JiGuoChao
	 * 
	 * @version 1.0
	 * 
	 * @date 2015-11-09
	 */
	private static class HorizontalScrollViewScrollDetector implements IScrollDetector {

		@Override
		public boolean canScrollHorizontal(View v, int direction) {
			HorizontalScrollView horizontalScrollView = (HorizontalScrollView) v;
			final int scrollX = horizontalScrollView.getScrollX();

			// 没有子控件就不需要滑动
			if (0 == horizontalScrollView.getChildCount()) {
				return false;
			}
			return (direction < 0 && scrollX < horizontalScrollView.getChildAt(0)
					.getWidth() - horizontalScrollView.getWidth())
					|| (direction > 0 && scrollX > 0);
		}

		@Override
		public boolean canScrollVertical(View v, int direction) {
			return false;
		}

	}

	/**
	 * 设置能够根据控件类型创造探测器的工厂
	 * 
	 * @param factory
	 */
	public static void setScrollDetectorFactory(IScrollDetectorFactory factory) {
		mFactory = factory;
	}

	/**
	 * 滑动探测器接口，探测是否能够朝某个方向滑动
	 * 
	 * @author JiGuoChao
	 * 
	 * @version 1.0
	 * 
	 * @date 2015-11-09
	 */
	public interface IScrollDetector {
		/**
		 * 探测一个View是否能继续横向滑动
		 * 
		 * @param v
		 *            探测的View
		 * @param direction
		 *            手指滑动方向left: direction<0, right: direction>0
		 * @return
		 */
		public boolean canScrollHorizontal(View v, int direction);

		/**
		 * 探测一个View是否能继续纵向滑动
		 * 
		 * @param v
		 *            探测的View
		 * @param direction
		 *            手指滑动方向bottom: direction<0, top: direction>0
		 * @return
		 */
		public boolean canScrollVertical(View v, int direction);
	}
}
