package com.morgan.library.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.morgan.library.R;

/**
 * 一个可以滑动Item看到下面菜单的ListView。还可以显示撤销操作的提示。并在用户触摸其他地方时关闭打开菜单的Item.缺点就是在ListView不是match_parent时候触摸其他区域不能关闭菜单，
 * 而当ListView是match_parent的时候最后一条分割线无法显示。
 * 
 * @author Morgan.Ji
 * 
 */
public class SwipeListView extends RelativeLayout {

    private ListView mListView;
    private View mCurrentItemView;
    private Animation mOpenAnimation, mCloseAnimation;

    private Context mContext;
    private LinearLayout mProgressBar;

    private PopupWindow mUndoPopup;
    private TextView mUndoText;
    private Button mUndoButton;
    private float mDensity;

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context c) {
        mContext = c;
        LayoutInflater.from(mContext).inflate(R.layout.swipe_listview, this, true);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnScrollListener(mOnScrollListener);
        mListView.setOnTouchListener(mOnTouchListener);
        mProgressBar = (LinearLayout) findViewById(R.id.profress_layout);
        initPopupView();
        initAnimation();
    }

    private void initAnimation() {
        mOpenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mOpenAnimation.setInterpolator(new DecelerateInterpolator());
        mOpenAnimation.setDuration(300);
        mOpenAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (null != mCurrentItemView) {
                    mCurrentItemView.findViewById(R.id.front).setVisibility(INVISIBLE);
                }
            }
        });
        mCloseAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mCloseAnimation.setInterpolator(new DecelerateInterpolator());
        mCloseAnimation.setDuration(450);
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            closeAnimate();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    };

    private float mStartX, mStartY;
    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mStartX) > 18 || Math.abs(event.getY() - mStartY) > 18) {
                    closeAnimate();
                }
                break;
            default:
                break;
            }
            return false;
        }
    };

    private void initPopupView() {
        mDensity = mContext.getResources().getDisplayMetrics().density;
        View v = LayoutInflater.from(mContext).inflate(R.layout.undo_popup, null);
        mUndoButton = (Button) v.findViewById(R.id.undo);
        mUndoText = (TextView) v.findViewById(R.id.text);
        mUndoPopup = new PopupWindow(v);
        mUndoPopup.setAnimationStyle(R.style.fade_animation);
        // Get scren width in dp and set width respectively
        int xdensity = (int) (mContext.getResources().getDisplayMetrics().widthPixels / mDensity);
        if (xdensity < 300) {
            mUndoPopup.setWidth((int) (mDensity * 280));
        } else if (xdensity < 350) {
            mUndoPopup.setWidth((int) (mDensity * 300));
        } else if (xdensity < 500) {
            mUndoPopup.setWidth((int) (mDensity * 330));
        } else {
            mUndoPopup.setWidth((int) (mDensity * 450));
        }
        mUndoPopup.setHeight((int) (mDensity * 48));
        mUndoPopup.setOutsideTouchable(true);
        mUndoPopup.setBackgroundDrawable(new ColorDrawable(android.R.color.white));
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void showUndoPopup(OnDismissListener listener, String text, OnClickListener click) {
        mUndoText.setText(text);
        mUndoPopup.showAtLocation(this, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 12);
        mUndoPopup.setOnDismissListener(listener);
        mUndoButton.setOnClickListener(click);
    }

    public void dismissUndoPopup() {
        mUndoPopup.dismiss();
    }

    public ListView getListView() {
        return mListView;
    }

    public void setEmptyView(View emptyView) {
        ViewGroup parentView = (ViewGroup) mListView.getParent();
        parentView.addView(emptyView, 1);
        mListView.setEmptyView(emptyView);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        mListView.setOnItemClickListener(mItemClickListener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mItemLongClickListener) {
        mListView.setOnItemLongClickListener(mItemLongClickListener);
    }

    public void hideDivider() {
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
    }

    public boolean openAnimate(View view) {
        if (null != mCurrentItemView) {
            if (view == mCurrentItemView) {
                return false;
            } else {
                closeAnimate();
            }
        }
        mCurrentItemView = view;
        mCurrentItemView.findViewById(R.id.back).setVisibility(VISIBLE);
        mCurrentItemView.findViewById(R.id.front).startAnimation(mOpenAnimation);
        return true;
    }

    public void closeAnimate() {
        if (null != mCurrentItemView) {
            mCurrentItemView.findViewById(R.id.back).setVisibility(GONE);
            mCurrentItemView.findViewById(R.id.front).setVisibility(VISIBLE);
            mCurrentItemView.findViewById(R.id.front).startAnimation(mCloseAnimation);
            mCurrentItemView = null;
        }
    }

    public void closeWithoutAnimate() {
        if (null != mCurrentItemView) {
            mCurrentItemView.findViewById(R.id.back).setVisibility(GONE);
            View view = mCurrentItemView.findViewById(R.id.front);
            view.setVisibility(VISIBLE);
            mCurrentItemView = null;
        }
    }

    public boolean openAnimate(int position) {
        View view = mListView.getChildAt(position + mListView.getHeaderViewsCount());
        return openAnimate(view);
    }
}
