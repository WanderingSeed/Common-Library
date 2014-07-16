package com.morgan.library.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.morgan.library.R;

/**
 * 一个自定义的Toast,还可以控制提示音的播放。
 * 
 * @author Morgan.Ji
 * 
 */
public class CustomToast extends Toast {

	private MediaPlayer mPlayer;
	private boolean mNeedSound;

	public CustomToast(Context context) {
		this(context, false);
	}

	public CustomToast(Context context, boolean needSound) {
		super(context);

		this.mNeedSound = needSound;

		mPlayer = MediaPlayer.create(context, R.raw.customtoast);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});

	}

	@Override
	public void show() {
		super.show();

		if (mNeedSound) {
			mPlayer.start();
		}
	}

	/**
	 * 设置是否播放声音
	 */
	public void setNeedSound(boolean needSound) {
		this.mNeedSound = needSound;
	}

	/**
	 * 获取控件实例
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @param needSound
	 *            是否播放声音
	 * @return
	 */
	public static CustomToast makeText(Context context, CharSequence text,
			boolean needSound) {
		CustomToast result = new CustomToast(context, needSound);

		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		DisplayMetrics dm = context.getResources().getDisplayMetrics();

		View v = inflate.inflate(R.layout.custom_toast, null);
		v.setMinimumWidth(dm.widthPixels);// 设置控件最小宽度为手机屏幕宽度

		TextView tv = (TextView) v.findViewById(R.id.toast_message);
		tv.setText(text);

		result.setView(v);
		result.setDuration(600);
		result.setGravity(Gravity.TOP, 0, (int) (dm.density * 75));

		return result;
	}

}
