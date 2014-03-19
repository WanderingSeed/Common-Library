package com.morgan.library.model;

/**
 * {@link com.morgan.library.widget.PopupMenuWidget}里面ListView的model类。
 * 
 * @author Morgan.Ji
 * 
 */
public class SpinnerItem {

    private int mResId;
    private String mText;
    private boolean mHideImg;

    public int getResId() {
        return mResId;
    }

    public void setResId(int mResId) {
        this.mResId = mResId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public boolean isHideImg() {
        return mHideImg;
    }

    public void setHideImg(boolean mHideImg) {
        this.mHideImg = mHideImg;
    }

}
