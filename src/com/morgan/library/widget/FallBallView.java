package com.morgan.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * 一个自由落体后弹几下的ImageView
 * @author Morgan.Ji
 *
 */
public class FallBallView extends ImageView {

    private static final int ANIMATION_MOVE = 0;
    private static final int ANIMATION_END = -1;
    private static final int DIVID_COUNT = 100;
    private static final int TRANSLATE_TIME = 1000;
    private int mStartX, mStartY;
    private int mEndX, mEndY;
    private Interpolator mInterpolator;
    private FallListener mOnFallListener;

    public FallBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInterpolator = new BounceInterpolator();
    }

    public void start()
    {
        if (null != mOnFallListener) {
            mOnFallListener.onfallStart();
        }
        new FallThread().start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg)
        {
            if (msg.what == ANIMATION_MOVE) {
                FallBallView.this.setTranslationX(msg.arg1);
                FallBallView.this.setTranslationY(msg.arg2);
            } else if (msg.what == ANIMATION_END) {
                FallBallView.this.setTranslationX(0);
                FallBallView.this.setTranslationY(0);
                FallBallView.this.setVisibility(View.INVISIBLE);
                if (null != mOnFallListener) {
                    mOnFallListener.onfalled();
                }
            }
        }
    };

    class FallThread extends Thread {

        @Override
        public void run()
        {
            int xLength = mEndX - mStartX;
            int yLength = mEndY - mStartY;
            int start = 1;
            while (start <= DIVID_COUNT) {
                try {
                    Message msg = mHandler.obtainMessage();
                    msg.what = ANIMATION_MOVE;
                    msg.arg1 = (int)(xLength * ((double)start / DIVID_COUNT));
                    msg.arg2 = (int)(yLength * mInterpolator.getInterpolation((float)((double)start / DIVID_COUNT)));
                    mHandler.sendMessage(msg);
                    Thread.sleep(TRANSLATE_TIME / DIVID_COUNT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                start++;
            }
            mHandler.sendEmptyMessage(ANIMATION_END);
        }
    }

    public void setStartX(int mStartX)
    {
        this.mStartX = mStartX;
    }

    public void setStartY(int mStartY)
    {
        this.mStartY = mStartY;
    }

    public void setEndX(int mEndX)
    {
        this.mEndX = mEndX;
    }

    public void setEndY(int mEndY)
    {
        this.mEndY = mEndY;
    }


    public void setOnFallListener(FallListener mOnFallListener)
    {
        this.mOnFallListener = mOnFallListener;
    }

    public interface FallListener {

        public void onfallStart();

        public void onfalled();
    }
}
