package com.morgan.library.widget.dialog;

import java.util.Calendar;
import java.util.StringTokenizer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.morgan.library.R;
import com.morgan.library.utils.StrUtils;

/**
 * 一个通用的时间选取器。
 * 
 * @author Morgan.Ji
 * 
 */
public class DatePickerWidget extends Dialog {

	private NumberPicker mYearPicker, mMonthPicker, mDayPicker;
	private TextView mTitleTextView, mFinishBtn;
	private android.view.View.OnClickListener mOnFinishClickListener;
	private boolean mShowValueOnTitle = true;
	private String mTitle;
	private int mYear, mMonth, mDay;
	private int mMinYear, mMinMonth, mMinDay;
	private int mMaxYear, mMaxMonth, mMaxDay;
	private Calendar mCalendar;

	public DatePickerWidget(Context context) {
		super(context, R.style.picker_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_picker_dialog);

		mCalendar = Calendar.getInstance();
		mYearPicker = (NumberPicker) findViewById(R.id.yearPicker);
		mMonthPicker = (NumberPicker) findViewById(R.id.monthPicker);
		mDayPicker = (NumberPicker) findViewById(R.id.dayPicker);

		if (mMinYear != 0) {
			mYearPicker.setMinValue(mMinYear);
		} else {
			mYearPicker.setMinValue(mCalendar.get(Calendar.YEAR) - 100);
		}
		if (mMaxYear != 0) {
			mYearPicker.setMaxValue(mMaxYear);
		} else {
			mYearPicker.setMaxValue(mCalendar.get(Calendar.YEAR) + 100);
		}
		mYearPicker.setOnValueChangedListener(mOnYearChangeListener);
		mYearPicker.setFocusable(false);
		mYearPicker.setFocusableInTouchMode(false);
		mYearPicker.setWrapSelectorWheel(false);

		mMonthPicker.setFocusable(true);
		mMonthPicker.setFocusableInTouchMode(true);
		mMonthPicker.setOnValueChangedListener(mOnMonthChangeListener);

		mDayPicker.setFormatter(StrUtils.getTwoDigitFormatter());
		mDayPicker.setFocusable(true);
		mDayPicker.setFocusableInTouchMode(true);
		mDayPicker.setOnValueChangedListener(mOnDayChangeListener);

		mTitleTextView = (TextView) findViewById(R.id.title);
		mFinishBtn = (TextView) findViewById(R.id.finish);
		mFinishBtn.setOnClickListener(mFinishClickListener);

		if (mYear == 0) {
			mYear = mCalendar.get(Calendar.YEAR);
			mMonth = mCalendar.get(Calendar.MONTH) + 1;
			mDay = mCalendar.get(Calendar.DATE);
		}
		if (mMaxYear != 0 && mYear >= mMaxYear) {
			mYear = mMaxYear;
			if (mMonth >= mMaxMonth) {
				mMonth = mMaxMonth;
				if (mDay >= mMaxDay) {
					mDay = mMaxDay;
				}
			}
		}
		if (mMinYear != 0 && mYear <= mMinYear) {
			mYear = mMinYear;
			if (mMonth <= mMinMonth) {
				mMonth = mMinMonth;
				if (mDay <= mMinDay) {
					mDay = mMinDay;
				}
			}
		}
		setMonthRange();
		setDayRange();
		mYearPicker.setValue(mYear);
		mMonthPicker.setValue(mMonth);
		mDayPicker.setValue(mDay);

		if (!StrUtils.isEmpty(mTitle)) {
			mTitleTextView.setText(mTitle);
		}
		updateTitle();
		this.setOnDismissListener(mOnDissmissListener);
	}

	private OnDismissListener mOnDissmissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO
		}
	};

	private android.view.View.OnClickListener mFinishClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			DatePickerWidget.this.dismiss();
			mOnFinishClickListener.onClick(v);
		}
	};

	private void setDayRange() {
		int currentValue = mDayPicker.getValue();
		if (mYear == mMinYear && mMonth == mMinMonth) {
			mDayPicker.setValue(mMinDay);
			mDayPicker.setMinValue(mMinDay);
			mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
		} else if (mYear == mMaxYear && mMonth == mMaxMonth) {
			mDayPicker.setValue(1);
			mDayPicker.setMinValue(1);
			mDayPicker.setMaxValue(mMaxDay);
		} else {
			mDayPicker.setValue(1);
			mDayPicker.setMinValue(1);
			mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
		}
		if (currentValue > mDayPicker.getMaxValue()) {
			currentValue = mDayPicker.getMaxValue();
		}
		if (currentValue < mDayPicker.getMinValue()) {
			currentValue = mDayPicker.getMinValue();
		}
		mDayPicker.setValue(currentValue);
	}

	private void setMonthRange() {
		if (mYear == mMinYear) {
			mMonthPicker.setMinValue(mMinMonth);
			mMonthPicker.setMaxValue(12);
		} else if (mYear == mMaxYear) {
			mMonthPicker.setMinValue(1);
			mMonthPicker.setMaxValue(mMaxMonth);
		} else {
			mMonthPicker.setMinValue(1);
			mMonthPicker.setMaxValue(12);
		}
	}

	private void updateTitle() {
		if (mShowValueOnTitle) {
			mTitleTextView.setText(mYear + "年" + mMonth + "月" + mDay + "日");
		}
	}

	private OnValueChangeListener mOnYearChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mYear = newVal;
			mCalendar.set(Calendar.YEAR, newVal);
			updateDayPicker();
			updateTitle();
		}
	};

	private OnValueChangeListener mOnMonthChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mMonth = newVal;
			mCalendar.set(Calendar.MONTH, newVal - 1);
			updateDayPicker();
			updateTitle();
		}
	};

	private OnValueChangeListener mOnDayChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mDay = newVal;
			updateTitle();
		}
	};

	public void updateDayPicker() {
		setMonthRange();
		setDayRange();
	}

	public String getCurrentValue() {
		return mYear + "-" + format(mMonth) + "-" + format(mDay);
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setOnFinishClickListener(android.view.View.OnClickListener clickListener) {
		this.mOnFinishClickListener = clickListener;
	}

	public boolean isShowValueOnTitle() {
		return mShowValueOnTitle;
	}

	public void setShowValueOnTitle(boolean mShowValueOnTitle) {
		this.mShowValueOnTitle = mShowValueOnTitle;
	}

	public String format(int value) {
		String result;
		if (value < 10) {
			result = "0" + value;
		} else {
			result = String.valueOf(value);
		}
		return result;
	}

	public void setInitValue(String date) {
		if (!StrUtils.isEmpty(date) && mYearPicker == null) {
			StringTokenizer t = new StringTokenizer(date, "-");
			int size = t.countTokens();
			if (size >= 3) {
				mYear = Integer.valueOf(t.nextToken());
				mMonth = Integer.valueOf(t.nextToken());
				mDay = Integer.valueOf(t.nextToken());
			}
		}
	}

	public void setMinValue(String date) {
		if (!StrUtils.isEmpty(date)) {
			StringTokenizer t = new StringTokenizer(date, "-");
			int size = t.countTokens();
			if (size >= 3) {
				mMinYear = Integer.valueOf(t.nextToken());
				mMinMonth = Integer.valueOf(t.nextToken());
				mMinDay = Integer.valueOf(t.nextToken());
			}
		}
	}

	public void setMaxValue(String date) {
		if (!StrUtils.isEmpty(date)) {
			StringTokenizer t = new StringTokenizer(date, "-");
			int size = t.countTokens();
			if (size >= 3) {
				mMaxYear = Integer.valueOf(t.nextToken());
				mMaxMonth = Integer.valueOf(t.nextToken());
				mMaxDay = Integer.valueOf(t.nextToken());
			}
		}
	}
}
