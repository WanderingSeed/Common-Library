package com.morgan.library.model;

/**
 * {@link com.morgan.library.widget.PopupMenuWidget}里面ListView的model类。
 * 
 * @author Morgan.Ji
 * 
 */
public class SpinnerItem {

	private int mResId;// 图片资源id
	private String mText;// 显示文本
	private boolean mHideImg;// 是否显示图片

	/**
	 * 获取图片资源id
	 * 
	 * @return
	 */
	public int getResId() {
		return mResId;
	}

	/**
	 * 设置图片资源id
	 * 
	 * @param mResId
	 */
	public void setResId(int mResId) {
		this.mResId = mResId;
	}

	/**
	 * 获取文本值
	 * 
	 * @return
	 */
	public String getText() {
		return mText;
	}

	/**
	 * 设置文本值
	 * 
	 * @param mText
	 */
	public void setText(String mText) {
		this.mText = mText;
	}

	/**
	 * 获取图片是否隐藏
	 * 
	 * @return
	 */
	public boolean isHideImg() {
		return mHideImg;
	}

	/**
	 * 设置是否隐藏图片
	 * 
	 * @param mHideImg
	 */
	public void setHideImg(boolean mHideImg) {
		this.mHideImg = mHideImg;
	}

}
