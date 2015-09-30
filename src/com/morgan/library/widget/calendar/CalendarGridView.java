package com.morgan.library.widget.calendar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.morgan.library.R;
import com.morgan.library.widget.calendar.CalendarWidget.Day;

/**
 * 日历一个月界面的九宫格控件
 * 
 * @author JiGuoChao
 * 
 * @version 1.0
 * 
 * @date 2015-7-31
 */
public class CalendarGridView extends LinearLayout {

	private Context mContext;
	private ArrayList<TextView> mTextViews = new ArrayList<TextView>();
	private OnItemClickListener mOnItemClickListener;
	private List<Day> mDays;
	private int indexOfFirstDay;
	private int indexOfLastDay;

	public CalendarGridView(Context context) {
		super(context);
		mContext = context;
		initGirdView();
	}

	/**
	 * 初始化当前控件的各个组件
	 */
	private void initGirdView() {
		LayoutInflater.from(mContext).inflate(R.layout.calendar_grid, this);
		for (int i = 0; i < 42; i++) {
			TextView v = (TextView) findViewWithTag("" + i);
			v.setText("" + i);
			mTextViews.add(v);
		}
	}

	/**
	 * 设置要显示的日期列表
	 * 
	 * @param days
	 * @param indexOfFirstDay
	 * @param indexOfLastDay
	 */
	public void setListDay(ArrayList<Day> days, int indexOfFirstDay, int indexOfLastDay) {
		mDays = days;
		this.indexOfFirstDay = indexOfFirstDay;
		this.indexOfLastDay = indexOfLastDay;

		notifyDataSetChanged();
	}

	/**
	 * 更新控件显示
	 */
	public void notifyDataSetChanged() {
		int i = 0;
		for (TextView v : mTextViews) {
			Day day = getItem(i);
			v.setText(day.getDayOfMonth() + "");
			v.setBackgroundResource(android.R.color.transparent);
			if (i < indexOfFirstDay || i > indexOfLastDay) {
				v.setTextColor(getResources().getColor(R.color.calendar_other_day_color));
			} else {
				v.setTextColor(getResources().getColor(R.color.black));
			}

			if (day.isToday()) {
				v.setBackgroundResource(R.color.canlendar_divider_color);
			}

			if (day.isSelectedDay()) {
				v.setTextColor(getResources().getColor(R.color.white));
				v.setBackgroundResource(R.color.calendar_select_day_color);
			}

			i++;
		}
	}

	public Day getItem(int p) {
		return mDays.get(p);
	}

	/**
	 * 获取当前页显示的日期数
	 * 
	 * @return
	 */
	public int getCount() {
		return mDays.size();
	}

	/**
	 * 根据用户在下个月点击的本月的日期位置来判断该日期在当前月的位置
	 * 
	 * @param p
	 * @return
	 */
	public int selectedPositonToPos(int p) {
		if (getCount() - (indexOfLastDay + 1) > 7) {// 此处不可能等于7，如果等于7则第二个月的开头一星期全是上个月的日期
			return getCount() - 14 + p;
		} else {
			return getCount() - 7 + p;
		}
	}

	/**
	 * 根据用户点击屏幕的坐标判断出选择的是哪个日期
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int pointToPosition(int x, int y) {
		int line = y / (getHeight() / 6);
		int p = (int) (line * 7 + Math.ceil(x / (float) (getWidth() / 7)));
		if (mOnItemClickListener != null)
			mOnItemClickListener.onItemClick(null, this, p - 1, 0l);
		return p;
	}

	/**
	 * 设置每个日期项 的点击事件
	 * 
	 * @param l
	 */
	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}
}
