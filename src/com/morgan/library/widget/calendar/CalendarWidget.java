package com.morgan.library.widget.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.morgan.library.R;
import com.morgan.library.utils.StrUtils;

/**
 * 一个可以左右滑动的平铺的日历
 * 
 * @author Morgan.Ji
 * 
 */
public class CalendarWidget extends LinearLayout {

	// 判断手势用
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private int mYear;
	private int mMonth;
	private int mIndexOfFirstDay;
	private int mIndexOfLastDay;

	private ViewFlipper mViewFlipper;
	private CalendarGridView mLastMonthView;// 上一个月
	private CalendarGridView mCurrentMonthView;// 当前月
	private CalendarGridView mNextMonthView;// 下一个月

	// 动画
	private Animation mSlideLeftIn;
	private Animation mSlideLeftOut;
	private Animation mSlideRightIn;
	private Animation mSlideRightOut;

	private Context mContext;
	private ArrayList<Day> mDays = new ArrayList<Day>();
	private TextView mYearMonthTextView;
	private GestureDetector mGesture = null;
	private Date mCurrentSelectedDate = null;

	private boolean mInAnimation;
	private CalendarWidgetListener mCalendarWidgetListener;

	public interface CalendarWidgetListener {
		void onSelectedDate(Date date);
	}

	/**
	 * 设置日期选择监听器
	 * 
	 * @param l
	 */
	public void setCalendarWidgetListener(CalendarWidgetListener l) {
		mCalendarWidgetListener = l;
	}

	private AnimationListener mAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			mInAnimation = true;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 当动画完成后调用
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					updateGirdView();
					mInAnimation = false;
				}
			}, 50);
		}
	};

	public CalendarWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.widget_calendar_view, this);
		// Get first day of week based on locale and populate the day headers
		initDate();

		mViewFlipper = (ViewFlipper) findViewById(R.id.calender_flipper);
		mYearMonthTextView = (TextView) findViewById(R.id.year_month_text);

		updateGirdView();

		mSlideLeftIn = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_left_in);
		mSlideLeftOut = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_left_out);
		mSlideRightIn = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_right_in);
		mSlideRightOut = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_right_out);

		mSlideLeftIn.setAnimationListener(mAnimationListener);
		mSlideRightIn.setAnimationListener(mAnimationListener);

		mGesture = new GestureDetector(mContext, new GestureListener());
		mViewFlipper.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGesture.onTouchEvent(event);
				return true;
			}
		});
		updateYearMonthTitle();
	}

	/**
	 * 更新日历控件，切换前一月后一月和页面初始化时调用
	 */
	private void updateGirdView() {
		initList();
		if (mViewFlipper.getChildCount() > 0) {
			mViewFlipper.removeAllViews();
		}
		if (mLastMonthView == null) {
			mLastMonthView = new CalendarGridView(mContext);
		}
		Date firstDayOfMonth = getFirstDayOfMonth((mMonth == 1 ? mYear - 1 : mYear),
				(mMonth == 1 ? 12 : mMonth - 1));
		int indexOfFirstDay = getWeekDay(firstDayOfMonth);
		Date lastDayOfMonth = getLastDayOfMonth((mMonth == 1 ? mYear - 1 : mYear),
				(mMonth == 1 ? 12 : mMonth - 1));
		int indexOfLastDay = indexOfFirstDay
				+ getDaysBetweenDate(firstDayOfMonth, lastDayOfMonth);

		mLastMonthView.setListDay(initList(firstDayOfMonth, indexOfFirstDay),
				indexOfFirstDay, indexOfLastDay);
		if (mCurrentMonthView == null) {
			mCurrentMonthView = new CalendarGridView(mContext);
		}
		mCurrentMonthView.setListDay(mDays, mIndexOfFirstDay, mIndexOfLastDay);

		if (mNextMonthView == null)
			mNextMonthView = new CalendarGridView(mContext);
		firstDayOfMonth = getFirstDayOfMonth((mMonth == 12 ? mYear + 1 : mYear),
				(mMonth == 12 ? 1 : mMonth + 1));
		indexOfFirstDay = getWeekDay(firstDayOfMonth);
		lastDayOfMonth = getLastDayOfMonth((mMonth == 12 ? mYear + 1 : mYear),
				(mMonth == 12 ? 1 : mMonth + 1));
		indexOfLastDay = indexOfFirstDay
				+ getDaysBetweenDate(firstDayOfMonth, lastDayOfMonth);

		mNextMonthView.setListDay(initList(firstDayOfMonth, indexOfFirstDay),
				indexOfFirstDay, indexOfLastDay);
		mViewFlipper.addView(mCurrentMonthView);
		mViewFlipper.addView(mNextMonthView);
		mViewFlipper.addView(mLastMonthView);

		mCurrentMonthView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (position < mIndexOfFirstDay) {
					showPrePage();
					int preMonthPosition = mLastMonthView.selectedPositonToPos(position);
					mCurrentSelectedDate = mLastMonthView.getItem(preMonthPosition).date;
				} else if (position > mIndexOfLastDay) {
					int nextMonthPosition = -1;
					if (mCurrentMonthView.getCount() - (mIndexOfLastDay + 1) > 7) {
						nextMonthPosition = position % 14;
					} else {
						nextMonthPosition = position % 7;
					}
					showNextPage();
					mCurrentSelectedDate = mNextMonthView.getItem(nextMonthPosition).date;
				} else {
					mCurrentSelectedDate = mCurrentMonthView.getItem(position).date;
					mCurrentMonthView.notifyDataSetChanged();
				}
				if (mCalendarWidgetListener != null) {
					mCalendarWidgetListener.onSelectedDate(mCurrentSelectedDate);
				}
			}
		});
	}

	/**
	 * 显示前一页
	 */
	public void showPrePage() {
		if (mInAnimation) {
			return;
		}
		mViewFlipper.setInAnimation(mSlideRightIn);
		mViewFlipper.setOutAnimation(mSlideRightOut);
		mViewFlipper.showPrevious();

		updateYearAndMonth(false);
		updateYearMonthTitle();
	}

	/**
	 * 显示后一页
	 */
	public void showNextPage() {
		if (mInAnimation) {
			return;
		}
		mViewFlipper.setInAnimation(mSlideLeftIn);
		mViewFlipper.setOutAnimation(mSlideLeftOut);
		mViewFlipper.showNext();

		updateYearAndMonth(true);
		updateYearMonthTitle();
	}

	/**
	 * 
	 * 初始化日历打开时所显示的时间
	 * 
	 */
	private void initDate() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 初始化某个月份的日期列表
	 * 
	 * @param firstDayOfMonth
	 * @param indexOfFirstDay
	 * @return
	 */
	private ArrayList<Day> initList(Date firstDayOfMonth, int indexOfFirstDay) {
		ArrayList<Day> days = new ArrayList<Day>();
		int listSize = 42;
		// 如果本月第一天不是周日，在日志对象数组最前面填入上月日志对象
		if (indexOfFirstDay > 0) {
			for (int i = 0; i < indexOfFirstDay; i++) {
				Day day = new Day(firstDayOfMonth.getTime() - (indexOfFirstDay - i)
						* 86400000);
				days.add(day);
			}
		}
		// 填上剩下的所有日期
		long monthStart = firstDayOfMonth.getTime();
		for (int i = indexOfFirstDay; i < listSize; i++) {
			long mills = monthStart + (i - indexOfFirstDay) * (long) 86400000;
			Day day = new Day(mills);
			days.add(day);
		}
		return days;
	}

	/**
	 * 初始化当前选中月份的日期列表
	 */
	private void initList() {
		mDays.clear();
		Date firstDayOfMonth = getFirstDayOfMonth(mYear, mMonth);
		mIndexOfFirstDay = getWeekDay(firstDayOfMonth);
		Date lastDayOfMonth = getLastDayOfMonth(mYear, mMonth);
		mIndexOfLastDay = mIndexOfFirstDay
				+ getDaysBetweenDate(firstDayOfMonth, lastDayOfMonth);

		int listSize = 42;
		// 如果本月第一天不是周一，在日志对象数组最前面填入上月日志对象
		if (mIndexOfFirstDay > 0) {
			for (int i = 0; i < mIndexOfFirstDay; i++) {
				Day day = new Day(firstDayOfMonth.getTime() - (mIndexOfFirstDay - i)
						* 86400000);
				mDays.add(day);
			}
		}

		// 填上剩下的所有日期
		long monthStart = firstDayOfMonth.getTime();
		for (int i = mIndexOfFirstDay; i < listSize; i++) {
			long mills = monthStart + (i - mIndexOfFirstDay) * (long) 86400000;
			Day day = new Day(mills);
			mDays.add(day);
		}
	}

	/**
	 * 更新日历头部
	 */
	public void updateYearMonthTitle() {
		String strYearMonth = mMonth > 9 ? mYear + "-" + mMonth : mYear + "-0" + mMonth;
		mYearMonthTextView.setText(strYearMonth);
	}

	/**
	 * 更新年月,加一月或者减一月
	 * 
	 * @param isNextMonth
	 *            是不是下个月，false则是上个月
	 */
	private void updateYearAndMonth(boolean isNextMonth) {
		if (isNextMonth) {
			mMonth += 1;
		} else {
			mMonth -= 1;
		}
		if (mMonth > 12) {
			mYear += 1;
			mMonth -= 12;
		}
		if (mMonth <= 0) {
			mYear -= 1;
			mMonth += 12;
		}
	}

	/**
	 * 选中指定日期
	 * 
	 * @param date
	 */
	public void selectDate(String date) {
		if (!StrUtils.isEmpty(date)) {
			StringTokenizer t = new StringTokenizer(date, "-");
			int size = t.countTokens();
			if (size >= 3) {
				mYear = Integer.valueOf(t.nextToken());
				mMonth = Integer.valueOf(t.nextToken());
				int day = Integer.valueOf(t.nextToken());
				Calendar calendar = Calendar.getInstance();
				calendar.set(mYear, mMonth - 1, day);
				selectDate(calendar.getTime());
			}
		}
	}

	/**
	 * 选中指定日期
	 * 
	 * @param date
	 */
	public void selectDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH) + 1;
		mCurrentSelectedDate = date;

		updateGirdView();
		updateYearMonthTitle();
	}

	/**
	 * 获取一个月的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private Date getFirstDayOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		return calendar.getTime();
	}

	/**
	 * 获取一个月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private Date getLastDayOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 获取一个日期是周几
	 * 
	 * @param date
	 * @return
	 */
	private int getWeekDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// Calendar.DAY_OF_WEEK 顺序为： SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
		// THURSDAY, FRIDAY, SATURDAY.
		// 变换成：MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
		int dayOfWeekAmerican = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int dayOfWeekChinese;
		// SUNDAY, 数值为0，要排到最后面去，所以加6一周最后一天，而其他天减一，往前调整一天
		if (dayOfWeekAmerican == 0) {
			// dayOfWeekAmerican == 0 == SUNDAY
			dayOfWeekChinese = dayOfWeekAmerican + 6;
		} else {
			dayOfWeekChinese = dayOfWeekAmerican - 1;
		}
		return dayOfWeekChinese;
	}

	/**
	 * 获取两个日期之间的的天数
	 * 
	 * @param firstDay
	 * @param lastDay
	 * @return
	 */
	private int getDaysBetweenDate(Date firstDay, Date lastDay) {
		return (int) ((lastDay.getTime() - firstDay.getTime()) / 86400000);
	}

	/**
	 * 日期类，提供是否为当前选中日期或是否为当前日期的判断
	 * 
	 * @author JiGuoChao
	 * 
	 * @version 1.0
	 * 
	 * @date 2015-7-31
	 */
	public class Day {
		private Date date;

		public Day(long milliseconds) {
			setDate(milliseconds);
		}

		public void setDate(Long milliseconds) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(milliseconds);
			this.date = c.getTime();
		}

		/**
		 * 获取一天在当月是第几天
		 * 
		 * @return
		 */
		public int getDayOfMonth() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.date);
			return calendar.get(Calendar.DAY_OF_MONTH);
		}

		/**
		 * 当前对象是否为当前日期
		 * 
		 * @return
		 */
		public boolean isToday() {
			Calendar calander1 = Calendar.getInstance();
			Calendar calander2 = Calendar.getInstance();
			calander1.setTime(this.date);
			Date date2 = new Date();
			calander2.setTime(date2);
			Boolean ret = calander1.get(Calendar.YEAR) == calander2.get(Calendar.YEAR)
					&& calander1.get(Calendar.MONTH) == calander2.get(Calendar.MONTH)
					&& calander1.get(Calendar.DATE) == calander2.get(Calendar.DATE);
			return ret;
		}

		/**
		 * 当前对象值是否为选中日期
		 * 
		 * @return
		 */
		public boolean isSelectedDay() {
			if (mCurrentSelectedDate == null) {
				return false;
			}
			Calendar calander1 = Calendar.getInstance();
			Calendar calander2 = Calendar.getInstance();
			calander1.setTime(this.date);
			calander2.setTime(mCurrentSelectedDate);
			Boolean ret = calander1.get(Calendar.YEAR) == calander2.get(Calendar.YEAR)
					&& calander1.get(Calendar.MONTH) == calander2.get(Calendar.MONTH)
					&& calander1.get(Calendar.DATE) == calander2.get(Calendar.DATE);
			return ret;

		}
	}

	/**
	 * 手续监听器，用于支持每月之间的滑动，和点击哪个日期的监听
	 * 
	 * @author JiGuoChao
	 * 
	 * @version 1.0
	 * 
	 * @date 2015-7-31
	 */
	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					showNextPage();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					showPrePage();
					return true;
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			mCurrentMonthView.pointToPosition((int) e.getX(), (int) e.getY());
			return false;
		}

	}
}
