package com.morgan.library.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.morgan.library.R;
import com.morgan.library.utils.FileUtils;
import com.morgan.library.utils.StrUtils;
import com.morgan.library.widget.numberpicker.NumberPicker;
import com.morgan.library.widget.numberpicker.NumberPicker.OnValueChangeListener;

/**
 * 一个地址选取器。
 * 
 * @author Morgan.Ji
 * 
 */
public class CityPickerWidget extends Dialog {

	private NumberPicker mProvincePicker, mCityPicker, mAreaPicker;
	private TextView mTitleTextView, mFinishBtn;
	private android.view.View.OnClickListener mOnFinishClickListener;
	private boolean mShowValueOnTitle = true;
	private String mTitle;
	private String mProvince, mCity, mArea;
	private List<String> mProvinces = new ArrayList<String>();
	private List<List<String>> mCitys = new ArrayList<List<String>>();
	private List<List<List<String>>> mAreas = new ArrayList<List<List<String>>>();

	private List<String> mCurrentCitys = new ArrayList<String>();
	private List<List<String>> mCurrentCityAreas = new ArrayList<List<String>>();
	private List<String> mCurrentAreas = new ArrayList<String>();

	public CityPickerWidget(Context context) {
		super(context, R.style.picker_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_picker_dialog);

		mProvinces = FileUtils.getAllProvinces();
		mCitys = FileUtils.getAllCities();
		mAreas = FileUtils.getAllAreas();

		mProvincePicker = (NumberPicker) findViewById(R.id.provincePicker);
		mCityPicker = (NumberPicker) findViewById(R.id.cityPicker);
		mAreaPicker = (NumberPicker) findViewById(R.id.areaPicker);
		mProvincePicker.setMinValue(0);
		mProvincePicker.setOnValueChangedListener(mOnProvinceChangeListener);
		mProvincePicker.setDisplayedValues(mProvinces
				.toArray(new String[mProvinces.size()]));
		mProvincePicker.setMaxValue(mProvinces.size() - 1);
		mProvincePicker.setFocusable(false);
		mProvincePicker.setFocusableInTouchMode(false);
		mProvincePicker.setWrapSelectorWheel(false);
		mProvincePicker.setMaxTextLength(4);
		mProvincePicker.setInputAble(false);

		mCityPicker.setMinValue(0);
		mCityPicker.setFocusable(true);
		mCityPicker.setFocusableInTouchMode(true);
		mCityPicker.setOnValueChangedListener(mOnCityChangeListener);
		mCityPicker.setMaxTextLength(4);
		mCityPicker.setInputAble(false);

		mAreaPicker.setMinValue(0);
		mAreaPicker.setFocusable(true);
		mAreaPicker.setFocusableInTouchMode(true);
		mAreaPicker.setOnValueChangedListener(mOnAreaChangeListener);
		mAreaPicker.setMaxTextLength(4);
		mAreaPicker.setInputAble(false);

		mTitleTextView = (TextView) findViewById(R.id.title);
		mFinishBtn = (TextView) findViewById(R.id.finish);
		mFinishBtn.setOnClickListener(mFinishClickListener);

		int index = 0, cityIndex = 0, areaIndex = 0;
		if (null == mProvince || null == mCity) {
			index = (mProvinces.size() - 1) / 2;
			mCurrentCitys = mCitys.get(index);
			mCurrentCityAreas = mAreas.get(index);
			cityIndex = (mCurrentCitys.size() - 1) / 2;
			mCurrentAreas = mCurrentCityAreas.get(cityIndex);
			areaIndex = (mCurrentAreas.size() - 1) / 2;
			if (areaIndex < 0) {
				areaIndex = 0;
			}
		} else {
			index = mProvinces.indexOf(mProvince);
			if (index < 0) {
				index = (mProvinces.size() - 1) / 2;
			}
			mCurrentCitys = mCitys.get(index);
			cityIndex = mCurrentCitys.indexOf(mCity);
			if (cityIndex < 0) {
				cityIndex = (mCurrentCitys.size() - 1) / 2;
			}
			mCurrentCityAreas = mAreas.get(index);
			mCurrentAreas = mCurrentCityAreas.get(cityIndex);
			areaIndex = mCurrentAreas.indexOf(mArea);
			if (areaIndex < 0) {
				areaIndex = (mCurrentAreas.size() - 1) / 2;
				if (areaIndex < 0) {
					areaIndex = 0;
				}
			}
		}
		mProvince = mProvinces.get(index);
		mCity = mCurrentCitys.get(cityIndex);
		if (mCurrentAreas.size() > 0) {
			mArea = mCurrentAreas.get(areaIndex);
		} else {
			mArea = "";
		}

		mProvincePicker.setValue(index);
		mCityPicker.setDisplayedValues(mCurrentCitys
				.toArray(new String[mCurrentCitys.size()]));
		mCityPicker.setMaxValue(mCurrentCitys.size() - 1);
		mCityPicker.setValue(cityIndex);
		mCityPicker.setWrapSelectorWheel(false);

		mAreaPicker.setDisplayedValues(mCurrentAreas
				.toArray(new String[mCurrentAreas.size()]));
		mAreaPicker
				.setMaxValue(mCurrentAreas.size() > 0 ? mCurrentAreas.size() - 1
						: 0);
		mAreaPicker.setValue(areaIndex);
		mAreaPicker.setWrapSelectorWheel(false);

		if (!StrUtils.isEmpty(mTitle)) {
			mTitleTextView.setText(mTitle);
		}
		updateTitle();
	}

	private android.view.View.OnClickListener mFinishClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mOnFinishClickListener.onClick(v);
			CityPickerWidget.this.dismiss();
		}
	};

	/**
	 * Updates the city wheel
	 */
	private void updateCities(List<String> cities) {
		mCityPicker.setValue(0);
		mCityPicker.setMaxValue(0);
		mCityPicker
				.setDisplayedValues(cities.toArray(new String[cities.size()]));
		mCityPicker.setMaxValue(cities.size() - 1);
		mCityPicker.setValue((cities.size() - 1) / 2);
		mCityPicker.invalidate();
		mCity = mCurrentCitys.get((cities.size() - 1) / 2);
		mCurrentAreas = mCurrentCityAreas.get((cities.size() - 1) / 2);
		mCityPicker.setWrapSelectorWheel(false);
		updateAreas(mCurrentAreas);
	}

	/**
	 * Updates the area wheel
	 */
	private void updateAreas(List<String> areas) {
		mAreaPicker.setValue(0);
		mAreaPicker.setMaxValue(0);
		mAreaPicker.setDisplayedValues(areas.toArray(new String[areas.size()]));
		mAreaPicker.setMaxValue(areas.size() > 0 ? areas.size() - 1 : 0);
		mAreaPicker.setValue(areas.size() > 0 ? (areas.size() - 1) / 2 : 0);
		mAreaPicker.invalidate();
		if (areas.size() > 0) {
			mArea = mCurrentAreas.get((areas.size() - 1) / 2);
		} else {
			mArea = "";
		}
		mAreaPicker.setWrapSelectorWheel(false);
		updateTitle();
	}

	private void updateTitle() {
		if (mShowValueOnTitle) {
			mTitleTextView.setText(mProvince + " " + mCity + " " + mArea);
		}
	}

	private OnValueChangeListener mOnProvinceChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mProvince = mProvinces.get(newVal);
			mCurrentCitys = mCitys.get(newVal);
			mCurrentCityAreas = mAreas.get(newVal);
			updateCities(mCurrentCitys);
		}
	};

	private OnValueChangeListener mOnCityChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mCity = mCurrentCitys.get(newVal);
			mCurrentAreas = mCurrentCityAreas.get(newVal);
			updateAreas(mCurrentAreas);
		}
	};

	private OnValueChangeListener mOnAreaChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mArea = mCurrentAreas.get(newVal);
			updateTitle();
		}
	};

	public void setInitValue(String province, String city, String area) {
		if (null == mProvincePicker) {
			mProvince = province;
			mCity = city;
			mArea = area;
		}
	}

	public String getCurrentValue() {
		return mProvince + " " + mCity + " " + mArea;
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
}
