package com.morgan.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.morgan.library.R;

/**
 * 一个通用的等待圆形进度条。
 * 
 * @author Morgan.Ji
 * 
 */
public class WaitScreenWidget extends Dialog {

	private LayoutInflater mInflater;
	private TextView mShowText;

	public WaitScreenWidget(Context context) {
		super(context, R.style.wait_screen);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.wait_screen, null);
		mShowText = (TextView) view.findViewById(R.id.wait_creen_show_text);
		mShowText.setText(context.getResources()
				.getString(R.string.loading_now));
		setContentView(view);
		setCanceledOnTouchOutside(false);
	}

	public void setShowText(String showText) {
		this.mShowText.setText(showText);
	}

	public void setShowText(int resId) {
		this.mShowText.setText(resId);
	}
}
