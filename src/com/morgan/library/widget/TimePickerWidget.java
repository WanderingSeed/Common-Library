package com.morgan.library.widget;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.morgan.library.R;
import com.morgan.library.utils.StrUtils;
import com.morgan.library.widget.numberpicker.NumberPicker;
import com.morgan.library.widget.numberpicker.NumberPicker.OnValueChangeListener;

/**
 * 一个时间选取器。
 * 
 * @author Morgan.Ji
 * 
 */
public class TimePickerWidget extends Dialog {

	private int DEFAULT_HOUR = 8, DEFAULT_MINUTE = 0;
	private NumberPicker mHourPicker, mMinutePicker;
	private TextView mTitleTextView, mFinishBtn;
	private android.view.View.OnClickListener mOnFinishClickListener;
	private boolean mShowValueOnTitle = true, mInitTimeByNow;
	private String mTitle;
	private int mHour, mMinute;

	public TimePickerWidget(Context context) {
		super(context, R.style.picker_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_picker_dialog);

		mHourPicker = (NumberPicker) findViewById(R.id.hourPicker);
		mMinutePicker = (NumberPicker) findViewById(R.id.minutePicker);
		mHourPicker.setMinValue(0);
		mHourPicker.setMaxValue(23);
		mHourPicker.setFormatter(NumberPicker.getTwoDigitFormatter());
		mHourPicker.setOnValueChangedListener(mOnHourChangeListener);
		mHourPicker.setFocusable(false);
		mHourPicker.setFocusableInTouchMode(false);

		mMinutePicker.setMinValue(0);
		mMinutePicker.setFocusable(true);
		mMinutePicker.setFocusableInTouchMode(true);
		mMinutePicker.setMaxValue(59);
		mMinutePicker.setFormatter(NumberPicker.getTwoDigitFormatter());
		mMinutePicker.setOnValueChangedListener(mOnMinuteChangeListener);

		mTitleTextView = (TextView) findViewById(R.id.title);
		mFinishBtn = (TextView) findViewById(R.id.finish);
		mFinishBtn.setOnClickListener(mFinishClickListener);

		if (mInitTimeByNow) {
			Calendar calendar = Calendar.getInstance();
			mHour = calendar.get(Calendar.HOUR_OF_DAY);
			mMinute = calendar.get(Calendar.MINUTE);
		} else {
			mHour = DEFAULT_HOUR;
			mMinute = DEFAULT_MINUTE;
		}
		mHourPicker.setValue(mHour);
		mMinutePicker.setValue(mMinute);

		if (!StrUtils.isEmpty(mTitle)) {
			mTitleTextView.setText(mTitle);
		}
		updateTitle();
		this.setOnDismissListener(mOnDissmissListener);
	}

	private OnDismissListener mOnDissmissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			mHourPicker.clearInputTextFocus();
			mMinutePicker.clearInputTextFocus();
		}
	};

	private android.view.View.OnClickListener mFinishClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mHourPicker.clearInputTextFocus();
			mMinutePicker.clearInputTextFocus();
			TimePickerWidget.this.dismiss();
			mOnFinishClickListener.onClick(v);
		}
	};

	private OnValueChangeListener mOnHourChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mHour = newVal;
			updateTitle();
		}
	};

	private OnValueChangeListener mOnMinuteChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			if (oldVal == mMinutePicker.getMaxValue()
					&& newVal == mMinutePicker.getMinValue()) {
				if (mHourPicker.getValue() < mHourPicker.getMaxValue()) {
					mHourPicker.setValue(mHourPicker.getValue() + 1);
				} else {
					mHourPicker.setValue(mHourPicker.getMinValue());
				}
			} else if (newVal == mMinutePicker.getMaxValue()
					&& oldVal == mMinutePicker.getMinValue()) {
				if (mHourPicker.getValue() > mHourPicker.getMinValue()) {
					mHourPicker.setValue(mHourPicker.getValue() - 1);
				} else {
					mHourPicker.setValue(mHourPicker.getMaxValue());
				}
			}
			mHour = mHourPicker.getValue();
			mMinute = newVal;
			updateTitle();
		}
	};

	private void updateTitle() {
		if (mShowValueOnTitle) {
			mTitleTextView.setText(format(mHour) + ":" + format(mMinute));
		}
	}

	public String getCurrentValue() {
		return format(mHour) + ":" + format(mMinute);
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setOnFinishClickListener(
			android.view.View.OnClickListener clickListener) {
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
}
