package com.morgan.library.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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

public class SwipeListView extends RelativeLayout {

    private ListView mListView;
    private View mCurrentItemView;
    Animation mOpenAnimation, mCloseAnimation;

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

    private void init(Context c)
    {
        mContext = c;
        LayoutInflater.from(mContext).inflate(R.layout.swipe_listview, this, true);
        mListView = (ListView)findViewById(R.id.list);
        mListView.setOnScrollListener(mOnScrollListener);
        mProgressBar = (LinearLayout)findViewById(R.id.profress_layout);
        initPopupView();
        initAnimation();
    }

    private void initAnimation()
    {
        mOpenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0);
        mOpenAnimation.setDuration(600);
        mOpenAnimation.setFillAfter(true);
        mCloseAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0);
        mCloseAnimation.setDuration(500);
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            if (null != mCurrentItemView) {
                closeAnimate();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
        }
    };

    private void initPopupView()
    {
        mDensity = mContext.getResources().getDisplayMetrics().density;
        View v = LayoutInflater.from(mContext).inflate(R.layout.undo_popup, null);
        mUndoButton = (Button)v.findViewById(R.id.undo);
        mUndoText = (TextView)v.findViewById(R.id.text);
        mUndoPopup = new PopupWindow(v);
        mUndoPopup.setAnimationStyle(R.style.fade_animation);
        // Get scren width in dp and set width respectively
        int xdensity = (int)(mContext.getResources().getDisplayMetrics().widthPixels / mDensity);
        if (xdensity < 300) {
            mUndoPopup.setWidth((int)(mDensity * 280));
        } else if (xdensity < 350) {
            mUndoPopup.setWidth((int)(mDensity * 300));
        } else if (xdensity < 500) {
            mUndoPopup.setWidth((int)(mDensity * 330));
        } else {
            mUndoPopup.setWidth((int)(mDensity * 450));
        }
        mUndoPopup.setHeight((int)(mDensity * 48));
        mUndoPopup.setOutsideTouchable(true);
        mUndoPopup.setBackgroundDrawable(new ColorDrawable(android.R.color.white));
    }

    public void hideProgressBar()
    {
        mProgressBar.setVisibility(View.GONE);
    }

    public void setAdapter(ListAdapter adapter, boolean showHeadView)
    {
        if (showHeadView) {
            View mHeadView = LayoutInflater.from(mContext).inflate(R.layout.actionbar_list_head_view, null);
            mListView.addHeaderView(mHeadView);
        }
        mListView.setAdapter(adapter);
    }

    public void setAdapter(ListAdapter adapter)
    {
        setAdapter(adapter, true);
    }

    public void showUndoPopup(OnDismissListener listener, String text, OnClickListener click)
    {
        mUndoText.setText(text);
        mUndoPopup.showAtLocation(this, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 12);
        mUndoPopup.setOnDismissListener(listener);
        mUndoButton.setOnClickListener(click);
    }

    public void dismissUndoPopup()
    {
        mUndoPopup.dismiss();
    }

    public ListView getListView()
    {
        return mListView;
    }

    public void setEmptyView(View emptyView)
    {
        ViewGroup parentView = (ViewGroup)mListView.getParent();
        parentView.addView(emptyView, 1);
        mListView.setEmptyView(emptyView);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener)
    {
        mListView.setOnItemClickListener(mItemClickListener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mItemLongClickListener)
    {
        mListView.setOnItemLongClickListener(mItemLongClickListener);
    }

    public void hideDivider()
    {
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
    }

    public boolean openAnimate(View view)
    {
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

    public void closeAnimate()
    {
        mCurrentItemView.findViewById(R.id.back).setVisibility(GONE);
        mCurrentItemView.findViewById(R.id.front).startAnimation(mCloseAnimation);
        mCurrentItemView = null;
    }

    public boolean openAnimate(int position)
    {
        View view = mListView.getChildAt(position + mListView.getHeaderViewsCount());
        return openAnimate(view);
    }

    public void closeAll()
    {
        if (null != mCurrentItemView) {
            closeAnimate();
        }
    }
}
