package com.morgan.library.widget;

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
 * 一个身高选取器。
 * 
 * @author Morgan.Ji
 * 
 */
public class HeightPickerWidget extends Dialog {

    private NumberPicker mPicker;
    private TextView mTitleTextView, mFinishBtn;
    private static final int FIRST_HEIGHT = 140;
    private static final int START_HEIGHT = 160;
    private static final int LAST_HEIGHT = 200;
    public static final String UNIT = "cm";
    private android.view.View.OnClickListener mOnFinishClickListener;
    private boolean mShowValueOnTitle = true;
    private String mTitle;
    private int mCurrentValue;

    public HeightPickerWidget(Context context) {
        super(context, R.style.picker_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_picker_dialog);
        mPicker = (NumberPicker) findViewById(R.id.numberPicker);
        mPicker.setMinValue(FIRST_HEIGHT);
        mPicker.setMaxValue(LAST_HEIGHT);
        mPicker.setValue(START_HEIGHT);
        mPicker.setFocusable(true);
        mPicker.setFocusableInTouchMode(true);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mFinishBtn = (TextView) findViewById(R.id.finish);
        mFinishBtn.setOnClickListener(mFinishClickListener);
        mPicker.setOnValueChangedListener(mOnValueChangeListener);
        mPicker.setUnit(UNIT);
        mPicker.setWrapSelectorWheel(false);
        if (!StrUtils.isEmpty(mTitle)) {
            mTitleTextView.setText(mTitle);
        }
        if (mCurrentValue != 0) {
            mPicker.setValue(mCurrentValue);
        }
        if (mShowValueOnTitle) {
            mTitleTextView.setText(mPicker.getValue() + UNIT);
        }
        this.setOnDismissListener(mOnDissmissListener);
    }

    private OnDismissListener mOnDissmissListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            mPicker.clearInputTextFocus();
        }
    };

    private android.view.View.OnClickListener mFinishClickListener = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mPicker.clearInputTextFocus();
            HeightPickerWidget.this.dismiss();
            mOnFinishClickListener.onClick(v);
        }
    };

    private OnValueChangeListener mOnValueChangeListener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (mShowValueOnTitle) {
                mTitleTextView.setText(newVal + UNIT);
            }
        }
    };

    public void setInitValue(int value) {
        if (null == mPicker) {
            mCurrentValue = value;
        }
    }

    public String getCurrentValue() {
        return mPicker.getValue() + UNIT;
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
}
