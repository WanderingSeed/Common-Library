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
	private CalendarGridView gView1;// 上一个月
	private CalendarGridView gView2;// 当前月
	private CalendarGridView gView3;// 下一个月

	// 动画
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;

	private int mSeletectDate = -1;
	private Context mContext;
	private ArrayList<Day> mDays = new ArrayList<Day>();
	TextView mYearMonthTextView;
	GestureDetector mGesture = null;
	Date mCurrentSelectedDate = null;

	private boolean inAnimation;
	private CalendarWidgetListener mCalendarWidgetListener;

	public interface CalendarWidgetListener {
		void onSelectedDate(Date date);
	}

	public void setCalendarWidgetListener(CalendarWidgetListener l) {
		mCalendarWidgetListener = l;
	}

	AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			inAnimation = true;
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
					CreateGirdView();
					inAnimation = false;
				}
			}, 100);
		}
	};

	public CalendarWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.calendar_widget_view,
				this);
		// Get first day of week based on locale and populate the day headers
		initDateTime();

		mViewFlipper = (ViewFlipper) findViewById(R.id.journal_calender_flipper);
		mYearMonthTextView = (TextView) findViewById(R.id.year_month_text);

		CreateGirdView();

		slideLeftIn = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(mContext,
				R.anim.calendar_slide_right_out);

		slideLeftIn.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);

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

	private void CreateGirdView() {
		initList();
		if (mViewFlipper.getChildCount() > 0) {
			mViewFlipper.removeAllViews();
		}
		if (gView1 == null)
			gView1 = new CalendarGridView(mContext);
		Date firstDayOfMonth = getFirstDayOfMonth((mMonth == 1 ? mYear - 1
				: mYear), (mMonth == 1 ? 12 : mMonth - 1));
		int f = getWeekDay(firstDayOfMonth);
		Date lastDayOfMonth = getLastDayOfMonth((mMonth == 1 ? mYear - 1
				: mYear), (mMonth == 1 ? 12 : mMonth - 1));
		int l = f + getDaysOfMonth(firstDayOfMonth, lastDayOfMonth);

		gView1.setListDay(initList(firstDayOfMonth, f), f, l);
		if (gView2 == null)
			gView2 = new CalendarGridView(mContext);
		if (mSeletectDate >= 0)
			gView2.setSelectedPositon(mSeletectDate);
		gView2.setListDay(mDays, mIndexOfFirstDay, mIndexOfLastDay);

		if (gView3 == null)
			gView3 = new CalendarGridView(mContext);
		firstDayOfMonth = getFirstDayOfMonth(
				(mMonth == 12 ? mYear + 1 : mYear), (mMonth == 12 ? 1
						: mMonth + 1));
		f = getWeekDay(firstDayOfMonth);
		lastDayOfMonth = getLastDayOfMonth((mMonth == 12 ? mYear + 1 : mYear),
				(mMonth == 12 ? 1 : mMonth + 1));
		l = f + getDaysOfMonth(firstDayOfMonth, lastDayOfMonth);

		gView3.setListDay(initList(firstDayOfMonth, f), f, l);
		mViewFlipper.addView(gView2);
		mViewFlipper.addView(gView3);
		mViewFlipper.addView(gView1);

		gView2.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Day day = null;
				if (position < mIndexOfFirstDay) {
					pre();
					int preMonthPosition = gView1
							.selectedPositonToPos(position);
					mSeletectDate = preMonthPosition;
					day = gView1.getItem(preMonthPosition);
				} else if (position > mIndexOfLastDay) {
					int nextMonthPosition = -1;
					if (gView2.getCount() - mIndexOfLastDay > 8) {
						nextMonthPosition = position % 14;
					} else {
						nextMonthPosition = position % 7;
					}
					next();
					mSeletectDate = nextMonthPosition;
					day = gView3.getItem(nextMonthPosition);
				} else {
					day = (Day) gView2.getItem(position);
					gView2.setSelectedPositon(position);
					mSeletectDate = position;
					mCurrentSelectedDate = day.date;
					gView2.notifyDataSetChanged();
				}
				mCurrentSelectedDate = day.date;
				if (mCalendarWidgetListener != null)
					mCalendarWidgetListener
							.onSelectedDate(mCurrentSelectedDate);
			}
		});
	}

	public void pre() {
		if (inAnimation)
			return;
		mSeletectDate = -1;
		mViewFlipper.setInAnimation(slideRightIn);
		mViewFlipper.setOutAnimation(slideRightOut);
		mViewFlipper.showPrevious();

		calculateYear(false);
		updateYearMonthTitle();
	}

	public void next() {
		if (inAnimation)
			return;
		mSeletectDate = -1;
		mViewFlipper.setInAnimation(slideLeftIn);
		mViewFlipper.setOutAnimation(slideLeftOut);
		mViewFlipper.showNext();

		calculateYear(true);
		updateYearMonthTitle();
	}

	/**
	 * 
	 * 初始化日历打开时所显示的时间
	 * 
	 * @param bundle
	 */
	private void initDateTime() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH) + 1;
	}

	private ArrayList<Day> initList(Date firstDayOfMonth, int f) {
		ArrayList<Day> days = new ArrayList<Day>();

		int listSize = 42;
		// 如果本月第一天不是周日，在日志对象数组最前面填入上月日志对象
		if (f > 0) {
			for (int i = 0; i < f; i++) {
				Day day = new Day(firstDayOfMonth.getTime() - (f - i)
						* 86400000);
				days.add(day);
			}
		}
		// 填上剩下的所有日期
		long monthStart = firstDayOfMonth.getTime();
		for (int i = f; i < listSize; i++) {

			long mills = monthStart + (i - f) * (long) 86400000;
			Day day = new Day(mills);
			days.add(day);
		}

		return days;
	}

	/**
	 * 初始化日历
	 */
	private void initList() {
		mDays.clear();
		Date firstDayOfMonth = getFirstDayOfMonth(mYear, mMonth);
		mIndexOfFirstDay = getWeekDay(firstDayOfMonth);
		Date lastDayOfMonth = getLastDayOfMonth(mYear, mMonth);
		mIndexOfLastDay = mIndexOfFirstDay
				+ getDaysOfMonth(firstDayOfMonth, lastDayOfMonth);

		int listSize = 42;
		// 如果本月第一天不是周日，在日志对象数组最前面填入上月日志对象
		if (mIndexOfFirstDay > 0) {
			for (int i = 0; i < mIndexOfFirstDay; i++) {
				Day day = new Day(firstDayOfMonth.getTime()
						- (mIndexOfFirstDay - i) * 86400000);
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

	public void updateYearMonthTitle() {
		String strYearMonth = mMonth + "月  " + mYear;
		mYearMonthTextView.setText(strYearMonth);
	}

	private void calculateYear(boolean isNext) {
		if (isNext) {
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

	public void showDate(String date) {
		if (!StrUtils.isEmpty(date)) {
			StringTokenizer t = new StringTokenizer(date, "-");
			int size = t.countTokens();
			if (size >= 3) {
				mYear = Integer.valueOf(t.nextToken());
				mMonth = Integer.valueOf(t.nextToken()) - 1;
				int day = Integer.valueOf(t.nextToken());
				Calendar calendar = Calendar.getInstance();
				calendar.set(mYear, mMonth, day);
				showDate(calendar.getTime());
			}
		}
	}

	public void showDate(Date date) {
		mSeletectDate = -1;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH) + 1;
		mCurrentSelectedDate = date;

		CreateGirdView();
		updateYearMonthTitle();
	}

	private Date getFirstDayOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		return calendar.getTime();
	}

	private Date getLastDayOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

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

	private int getDaysOfMonth(Date firstDay, Date lastDay) {
		return (int) ((lastDay.getTime() - firstDay.getTime()) / 86400000);
	}

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

		public int getDay() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.date);
			return calendar.get(Calendar.DAY_OF_MONTH);
		}

		public boolean isToday() {
			Calendar calander1 = Calendar.getInstance();
			Calendar calander2 = Calendar.getInstance();
			calander1.setTime(this.date);
			Date date2 = new Date();
			calander2.setTime(date2);
			Boolean ret = calander1.get(Calendar.YEAR) == calander2
					.get(Calendar.YEAR)
					&& calander1.get(Calendar.MONTH) == calander2
							.get(Calendar.MONTH)
					&& calander1.get(Calendar.DATE) == calander2
							.get(Calendar.DATE);
			return ret;
		}

		public boolean isSelectedDay() {
			if (mCurrentSelectedDate == null) {
				return false;
			}

			Calendar calander1 = Calendar.getInstance();
			Calendar calander2 = Calendar.getInstance();
			calander1.setTime(this.date);
			calander2.setTime(mCurrentSelectedDate);
			Boolean ret = calander1.get(Calendar.YEAR) == calander2
					.get(Calendar.YEAR)
					&& calander1.get(Calendar.MONTH) == calander2
							.get(Calendar.MONTH)
					&& calander1.get(Calendar.DATE) == calander2
							.get(Calendar.DATE);
			return ret;

		}
	}

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
					next();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					pre();
					return true;
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			gView2.pointToPosition((int) e.getX(), (int) e.getY());
			return false;
		}

	}
}
