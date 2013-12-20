package com.morgan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.morgan.library.R;

/**
 * This is a list view which can load data when user pull up and refresh data when pull down. Of course, you should set
 * OnLoadDataListener and OnRefreshDataListener, when finish load data or refresh data you should call the relate
 * method. If you don't want the refresh function, just don't set OnRefreshDataListener. in addition, you can set
 * yourself header view which can above or below the refresh view.
 * 
 * @author Morgan.Ji
 * 
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {

    private static final int REFRESH_VIEW_MAX_HEIGHT = 280;
    // State of pull down to refresh
    private final static int PULL_TO_REFRESH = 0X1001;
    // State of Can release to refresh
    private final static int RELEASE_TO_REFRESH = 0X1002;
    // State of refreshing
    private final static int REFRESHING = 0X1003;
    // State of Finish refresh
    private final static int DONE = 0X1004;

    // when activity finish refresh data, send this.
    public final static int SUCCESS_GET_DATA = 0X1005;
    // when activity finish load data, send this.
    public final static int FINISH_GET_DATA = 0X1006;
    // when error load data, send this.
    public final static int ERROR_GET_DATA = 0X1007;

    // The state of now.
    private int mStatus;
    // Use for ensure mStartY only have one value in an integrated
    // touch event.
    private boolean mIsRecored;
    private int mStartY;
    private int mFirstVisibleItemIndex;
    // Judge is finger on touch
    private int mScrollState;

    private Context mContext;
    private LayoutInflater mInflater;

    private LinearLayout mHeaderView;
    // User listView header which above refresh header
    private LinearLayout mUserTopHeaderView;
    // User listView header which below refresh header
    private LinearLayout mUserBottomHeaderView;
    private LinearLayout mRefreshHeaderView;
    private ProgressBar mHeaderProgressBar;
    private TextView mHeaderStatus;
    private ImageView mHeaderRefreshArrow;
    private RotateAnimation mRefreshArrowAnimation;
    private RotateAnimation mRefreshArrowReverseAnimation;

    private LinearLayout mFooterView;
    private ProgressBar mFooterProgressBar;
    private TextView mFooterStatus;

    private OnLoadDataListener mDataLoader;
    private OnRefreshDataListener mDataRefresher;

    private int mUserTopHeaderHeight;
    private int mUserHeaderHeight;
    private int mRefreshHeaderHeight;
    private int mRefreshHeaderOriginalTopPadding;

    // Is Load all data.
    private boolean mIsLoadAll;
    // Have refresh function
    private boolean mNeedRefreshFunction;
    // Is List view totally on it's top
    private boolean mIsOnTop;
    // Is load data finish.
    private boolean mIsFinished;
    // Refresh arrow is orient top
    private boolean mIsBack;
    // When list size is 0, show this message.
    private String mNoDataMessage;

    private boolean mNoDataInList;

    public interface OnLoadDataListener {
        public void onLoadData();
    }

    public interface OnRefreshDataListener {
        public void onRefreshData();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshListView);
        mNoDataMessage = ta.getString(R.styleable.PullToRefreshListView_noDataMessage);
        ta.recycle();
        this.mContext = context;
        init();
    }

    private void init() {
        mNeedRefreshFunction = false;
        mIsOnTop = true;
        mIsFinished = true;
        mFirstVisibleItemIndex = 0;
        mUserTopHeaderHeight = 0;
        mUserHeaderHeight = 0;
        mStatus = DONE;

        mRefreshArrowAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mRefreshArrowAnimation.setInterpolator(new LinearInterpolator());
        mRefreshArrowAnimation.setDuration(100);
        mRefreshArrowAnimation.setFillAfter(true);

        mRefreshArrowReverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mRefreshArrowReverseAnimation.setInterpolator(new LinearInterpolator());
        mRefreshArrowReverseAnimation.setDuration(100);
        mRefreshArrowReverseAnimation.setFillAfter(true);

        mInflater = LayoutInflater.from(mContext);

        mFooterView = (LinearLayout) mInflater.inflate(R.layout.pull_to_refresh_listview_footer, null);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pull_to_refresh_listview_footer_progress_bar);
        mFooterStatus = (TextView) mFooterView.findViewById(R.id.pull_to_refresh_listview_footer_status);
        mFooterProgressBar.setVisibility(View.GONE);

        mHeaderView = (LinearLayout) mInflater.inflate(R.layout.pull_to_refresh_listview_header, null);
        mUserTopHeaderView = (LinearLayout) mHeaderView
                .findViewById(R.id.pull_to_refresh_listview_user_above_progress_header);
        mUserBottomHeaderView = (LinearLayout) mHeaderView
                .findViewById(R.id.pull_to_refresh_listview_user_below_progress_header);
        mRefreshHeaderView = (LinearLayout) mHeaderView.findViewById(R.id.pull_to_refresh_listview_refresh_header);
        mHeaderProgressBar = (ProgressBar) mRefreshHeaderView
                .findViewById(R.id.pull_to_refresh_listview_header_progress_bar);
        mHeaderStatus = (TextView) mRefreshHeaderView.findViewById(R.id.pull_to_refresh_listview_header_status);
        mHeaderRefreshArrow = (ImageView) mRefreshHeaderView.findViewById(R.id.pull_to_refresh_listview_header_arrow);
        measureView(mRefreshHeaderView);
        mRefreshHeaderOriginalTopPadding = mRefreshHeaderView.getPaddingTop();
        mRefreshHeaderHeight = mRefreshHeaderView.getMeasuredHeight();
        mRefreshHeaderView.setPadding(mRefreshHeaderView.getPaddingLeft(), -1 * mRefreshHeaderHeight,
                mRefreshHeaderView.getPaddingRight(), mRefreshHeaderView.getPaddingBottom());
        mRefreshHeaderView.invalidate();
        this.addHeaderView(mHeaderView);
        this.addFooterView(mFooterView);
        mFooterView.setOnClickListener(mMoreDataListener);
    }

    private OnClickListener mMoreDataListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mIsLoadAll && mIsFinished && mDataLoader != null) {
                loadData();
            }
        }
    };

    public void addUserTopHeaderView(View v) {
        mUserTopHeaderView.removeAllViews();
        mUserTopHeaderView.addView(v);
        measureView(v);
        mUserTopHeaderHeight = v.getMeasuredHeight();
        mUserHeaderHeight += v.getMeasuredHeight();
    }

    public void addUserBottomHeaderView(View v) {
        mUserBottomHeaderView.removeAllViews();
        mUserBottomHeaderView.addView(v);
        measureView(v);
        mUserHeaderHeight += v.getMeasuredHeight();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItemIndex = firstVisibleItem;
        mIsOnTop = false;
        if (mFirstVisibleItemIndex == 0) {
            Rect mUserHeaderViewRect = new Rect();
            mHeaderView.getLocalVisibleRect(mUserHeaderViewRect);
            if (mUserHeaderViewRect.height() >= mUserHeaderHeight) {
                mIsOnTop = true;
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
        boolean scrollEnd = false;
        try {
            if (view.getPositionForView(mFooterView) == view.getLastVisiblePosition())
                scrollEnd = true;
        } catch (Exception e) {
            scrollEnd = false;
        }
        if (scrollEnd && !mIsLoadAll && mIsFinished && !mIsRecored && !mNoDataInList) {
            loadData();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (mIsOnTop && mNeedRefreshFunction && mIsFinished) {
                mStartY = (int) event.getY();
                if (mStartY > mUserTopHeaderHeight) {
                    mIsRecored = true;
                }
            }
            break;
        // lose focus or cancel action
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (mIsRecored) {
                if (mStatus == PULL_TO_REFRESH) {
                    mStatus = DONE;
                    changeHeaderViewByState();
                } else if (mStatus == RELEASE_TO_REFRESH) {
                    mStatus = REFRESHING;
                    changeHeaderViewByState();
                }
                mIsRecored = false;
            }
            break;

        case MotionEvent.ACTION_MOVE:
            int tempY = (int) event.getY();
            if (!mIsRecored && mIsOnTop && mNeedRefreshFunction && mIsFinished) {
                if (tempY > mUserTopHeaderHeight) {
                    mStartY = tempY;
                    mIsRecored = true;
                }
            }
            if (mIsRecored && mStatus != REFRESHING) {
                if (mStatus == RELEASE_TO_REFRESH) {
                    // the situation of push up
                    if ((tempY - mStartY < mRefreshHeaderHeight + 30) && (tempY - mStartY) > 0) {
                        mStatus = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                    } else if (tempY - mStartY <= 0) {
                        mStatus = DONE;
                        changeHeaderViewByState();
                        mIsRecored = false;
                    }
                } else if (mStatus == PULL_TO_REFRESH) {
                    if (tempY - mStartY >= mRefreshHeaderHeight + 30 && mScrollState == SCROLL_STATE_TOUCH_SCROLL) {
                        mStatus = RELEASE_TO_REFRESH;
                        changeHeaderViewByState();
                    } else if (tempY - mStartY <= 0) {
                        mStatus = DONE;
                        changeHeaderViewByState();
                        mIsRecored = false;
                    }
                } else if (mStatus == DONE) {
                    if (tempY - mStartY > 0) {
                        mStatus = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                    }
                }
                // refresh headView's paddingTop
                if (mStatus == RELEASE_TO_REFRESH || mStatus == PULL_TO_REFRESH
                        && mScrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    int topPadding;
                    if ((tempY - mStartY) <= REFRESH_VIEW_MAX_HEIGHT) {
                        topPadding = (int) ((tempY - mStartY - mRefreshHeaderHeight));
                    } else {
                        topPadding = REFRESH_VIEW_MAX_HEIGHT - mRefreshHeaderHeight;
                    }
                    mRefreshHeaderView.setPadding(mRefreshHeaderView.getPaddingLeft(), topPadding,
                            mRefreshHeaderView.getPaddingRight(), mRefreshHeaderView.getPaddingBottom());
                    mRefreshHeaderView.invalidate();
                    // keep list view on static, only scroll refresh view.
                    return true;
                }
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    private void changeHeaderViewByState() {
        switch (mStatus) {
        case RELEASE_TO_REFRESH:
            mHeaderProgressBar.setVisibility(View.GONE);
            mHeaderRefreshArrow.setVisibility(View.VISIBLE);
            mHeaderRefreshArrow.clearAnimation();
            mIsBack = true;
            mHeaderRefreshArrow.startAnimation(mRefreshArrowAnimation);
            mHeaderStatus.setText(R.string.refresh_after_release);
            break;
        case PULL_TO_REFRESH:
            mHeaderProgressBar.setVisibility(View.GONE);
            mHeaderRefreshArrow.setVisibility(View.VISIBLE);
            mHeaderRefreshArrow.clearAnimation();
            if (mIsBack) {
                mIsBack = false;
                mHeaderRefreshArrow.clearAnimation();
                mHeaderRefreshArrow.startAnimation(mRefreshArrowReverseAnimation);
            }
            mHeaderStatus.setText(R.string.pull_to_refresh);
            break;
        case REFRESHING:
            mHeaderRefreshArrow.setVisibility(View.GONE);
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            mHeaderStatus.setText(R.string.refreshing_now);
            mRefreshHeaderView.setPadding(mRefreshHeaderView.getPaddingLeft(), mRefreshHeaderOriginalTopPadding,
                    mRefreshHeaderView.getPaddingRight(), mRefreshHeaderView.getPaddingBottom());
            mRefreshHeaderView.invalidate();
            refreshData();
            break;
        case DONE:
            mIsBack = false;
            mHeaderRefreshArrow.setImageResource(R.drawable.icon_refresh_arrow);
            mHeaderProgressBar.setVisibility(View.GONE);
            mHeaderStatus.setText(R.string.pull_to_refresh);
            mRefreshHeaderView.setPadding(mRefreshHeaderView.getPaddingLeft(), -1 * mRefreshHeaderHeight,
                    mRefreshHeaderView.getPaddingRight(), mRefreshHeaderView.getPaddingBottom());
            mRefreshHeaderView.invalidate();
            break;
        }
    }

    private void loadData() {
        mIsFinished = false;
        mFooterProgressBar.setVisibility(View.VISIBLE);
        mFooterStatus.setText(getResources().getString(R.string.loading_now));
        mDataLoader.onLoadData();
    }

    private void refreshData() {
        mIsFinished = false;
        mDataRefresher.onRefreshData();
    }

    /**
     * measure headView's width and height value
     * 
     * @param child
     *            The view you want measure
     */
    @SuppressWarnings("deprecation")
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int heightSpec;
        if (lpHeight > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(widthSpec, heightSpec);
    }

    /**
     * Set a loadDataListener for the list view to load data. And at the same time load the first page data.
     * 
     * @param loadDataListener
     *            The load data interface instance.
     */
    public void setLoadDataListener(OnLoadDataListener loadDataListener) {
        this.mDataLoader = loadDataListener;
        loadData();
    }

    /**
     * Set a OnRefreshDataListener for the list view to refresh new data.
     * 
     * @param refreshDataListener
     *            The refresh data interface instance.
     */
    public void setRefreshDataListener(OnRefreshDataListener refreshDataListener) {
        this.mDataRefresher = refreshDataListener;
        mNeedRefreshFunction = true;
    }

    public void setNoDataMessage(String noDataMessage) {
        this.mNoDataMessage = noDataMessage;
    }

    public void getDataError() {
        handleMessage(ERROR_GET_DATA);
    }

    public void getPartialData() {
        handleMessage(SUCCESS_GET_DATA);
    }

    public void getAllData() {
        handleMessage(FINISH_GET_DATA);
    }

    /**
     * handle the message when there finished load or refresh data.
     */
    private void handleMessage(int message) {
        if (message == SUCCESS_GET_DATA || message == FINISH_GET_DATA || message == ERROR_GET_DATA) {
            if (mStatus == REFRESHING) {
                if (message != ERROR_GET_DATA) {
                    Toast.makeText(mContext, R.string.finish_refresh, Toast.LENGTH_SHORT).show();
                }
                mStatus = DONE;
                changeHeaderViewByState();
            } else {
                mFooterProgressBar.setVisibility(View.GONE);
            }
        }

        switch (message) {
        case SUCCESS_GET_DATA:
            mIsLoadAll = false;
            mFooterStatus.setText(R.string.more);
            break;
        case FINISH_GET_DATA:
            mIsLoadAll = true;
            mFooterStatus.setText(R.string.all_data_loaded);
            break;
        case ERROR_GET_DATA:
            mFooterStatus.setText(R.string.all_data_loaded);
            break;
        }
        if (message == SUCCESS_GET_DATA || message == FINISH_GET_DATA || message == ERROR_GET_DATA) {
            if (PullToRefreshListView.this.getAdapter() != null
                    && PullToRefreshListView.this.getAdapter().getCount() == 2) {
                mFooterStatus.setText(mNoDataMessage);
                mFooterView.setOnClickListener(null);
                mNoDataInList = true;
            } else {
                mNoDataInList = false;
                mFooterView.setOnClickListener(mMoreDataListener);
            }
            mIsFinished = true;
        }
    }
}
