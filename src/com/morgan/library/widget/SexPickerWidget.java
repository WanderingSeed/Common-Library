package com.morgan.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.morgan.library.R;
import com.morgan.library.widget.numberpicker.NumberPicker;
import com.morgan.library.widget.numberpicker.NumberPicker.OnValueChangeListener;

public class SexPickerWidget extends Dialog {

    private NumberPicker mSexPicker;
    private TextView mTitleTextView, mFinishBtn;
    private android.view.View.OnClickListener mOnFinishClickListener;
    private boolean mShowValueOnTitle = true;
    private String mValue;
    private String mTitle;
    private final String[] sex = new String[] { "男", "女" };

    public SexPickerWidget(Context context) {
        super(context, R.style.picker_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_picker_dialog);
        mSexPicker = (NumberPicker)findViewById(R.id.numberPicker);
        mSexPicker.setMinValue(0);
        mSexPicker.setMaxValue(1);
        mSexPicker.setDisplayedValues(sex);
        mSexPicker.setFocusable(true);
        mSexPicker.setFocusableInTouchMode(true);
        mSexPicker.setOnValueChangedListener(mOnValueChangeListener);
        mSexPicker.setWrapSelectorWheel(false);
        mSexPicker.setInputAble(false);
        mTitleTextView = (TextView)findViewById(R.id.title);
        mFinishBtn = (TextView)findViewById(R.id.finish);
        mFinishBtn.setOnClickListener(mFinishClickListener);
        if (null != mTitle) {
            mTitleTextView.setText(mTitle);
        }
        if (null == mValue) {
            mValue = sex[0];
        } else if (sex[1].equals(mValue)) {
            mSexPicker.setValue(1);
        }
        if (mShowValueOnTitle) {
            mTitleTextView.setText(mValue);
        }
    }

    private android.view.View.OnClickListener mFinishClickListener = new android.view.View.OnClickListener() {
        
        @Override
        public void onClick(View v)
        {
            SexPickerWidget.this.dismiss();
            mOnFinishClickListener.onClick(v);
        }
    };

    private OnValueChangeListener mOnValueChangeListener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal)
        {
            if (mShowValueOnTitle) {
                mTitleTextView.setText(sex[newVal]);
            }
        }
    };

    public void setInitValue(String value)
    {
        if (mSexPicker == null) {
            mValue = value;
        }
    }

    public String getCurrentValue()
    {
        return sex[mSexPicker.getValue()];
    }

    public void setTitle(String title)
    {
        if (null == mTitleTextView) {
            mTitle = title;
        } else {
            mTitleTextView.setText(title);
        }
    }

    public void setOnFinishClickListener(android.view.View.OnClickListener clickListener)
    {
        this.mOnFinishClickListener = clickListener;
    }

    public boolean isShowValueOnTitle()
    {
        return mShowValueOnTitle;
    }

    public void setShowValueOnTitle(boolean mShowValueOnTitle)
    {
        this.mShowValueOnTitle = mShowValueOnTitle;
    }
}
