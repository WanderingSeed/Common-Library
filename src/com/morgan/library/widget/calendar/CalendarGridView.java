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

public class CalendarGridView extends LinearLayout {

    private Context mContext;
    private ArrayList<TextView> mTextViews = new ArrayList<TextView>();
    private OnItemClickListener mOnItemClickListener;
    private List<Day> mDays;
    private int indexOfFirstDay;
    private int indexOfLastDay;
    transient int selectedPositon = -1;

    public CalendarGridView(Context context) {
        super(context);
        mContext = context;
        setGirdView();
    }

    private void setGirdView() {
        LayoutInflater.from(mContext).inflate(R.layout.calendar_grid, this);
        for (int i = 0; i < 42; i++) {
            TextView v = (TextView) findViewWithTag("" + i);
            v.setText("" + i);
            mTextViews.add(v);
        }
    }

    public void setListDay(ArrayList<Day> days, int indexOfFirstDay, int indexOfLastDay) {
        mDays = days;
        this.indexOfFirstDay = indexOfFirstDay;
        this.indexOfLastDay = indexOfLastDay;

        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        int i = 0;
        for (TextView v : mTextViews) {
            Day day = getItem(i);
            v.setText(day.getDay() + "");
            v.setTextColor(getResources().getColor(R.color.black));
            v.setBackgroundResource(android.R.color.transparent);
            if (i < indexOfFirstDay || i > indexOfLastDay) {
                v.setTextColor(getResources().getColor(R.color.calendar_other_day_color));
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

    public int getCount() {
        return mDays.size();
    }

    public void setSelectedPositon(int p) {
        selectedPositon = p;
    }

    public int selectedPositonToPos(int p) {
        if (getCount() - indexOfLastDay > 8) {
            return p + getCount() - 14;
        } else {
            return p + getCount() - 7;
        }
    }

    public int pointToPosition(int x, int y) {
        int line = y / (getHeight() / 6);
        int p = (int) (line * 7 + Math.ceil(x / (float) (getWidth() / 7)));
        if (mOnItemClickListener != null)
            mOnItemClickListener.onItemClick(null, this, p - 1, 0l);
        return p;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }
}
