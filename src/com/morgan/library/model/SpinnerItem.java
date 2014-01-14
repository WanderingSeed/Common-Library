package com.morgan.library.model;

public class SpinnerItem {

    private int mResId;
    private String mText;
    private boolean mHideImg;
    private int mItemId;

    public int getResId()
    {
        return mResId;
    }

    public void setResId(int mResId)
    {
        this.mResId = mResId;
    }

    public String getText()
    {
        return mText;
    }

    public void setText(String mText)
    {
        this.mText = mText;
    }

    public boolean isHideImg()
    {
        return mHideImg;
    }

    public void setHideImg(boolean mHideImg)
    {
        this.mHideImg = mHideImg;
    }

    public int getItemId()
    {
        return mItemId;
    }

    public void setItemId(int mItemId)
    {
        this.mItemId = mItemId;
    }
    
    

}
