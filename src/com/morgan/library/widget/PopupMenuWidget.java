package com.morgan.library.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.morgan.library.R;
import com.morgan.library.model.SpinnerItem;

/**
 * 一个基于View的弹出菜单。
 * @author Morgan.Ji
 *
 */
public class PopupMenuWidget {

    private Activity mContext;
    private ListView mListView;
    private PopupWindow mPopupWindow;
    private List<SpinnerItem> mPopupList = new ArrayList<SpinnerItem>();

    public PopupMenuWidget(Activity context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.popup_menu_widget_list, null);
        mListView = (ListView)view.findViewById(R.id.list);
        mListView.setAdapter(new PopupListAdapter());
        mListView.setFocusableInTouchMode(true);
        mListView.setFocusable(true);
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = 0;
        if (metrics.widthPixels >= 720) {
            width = 220;
        } else {
            width = 180;
        }
        mPopupWindow = new PopupWindow(view, width, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.white));
    }

    public PopupMenuWidget(Activity context, int width) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.popup_menu_widget_list, null);
        mListView = (ListView)view.findViewById(R.id.list);
        mListView.setAdapter(new PopupListAdapter());
        mListView.setFocusableInTouchMode(true);
        mListView.setFocusable(true);
        mPopupWindow = new PopupWindow(view, width, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.white));
    }

    public void showAsDropDown(View parent)
    {
        mPopupWindow.showAsDropDown(parent, mContext.getResources().getDimensionPixelSize(R.dimen.pop_xoff), mContext.getResources().getDimensionPixelSize(R.dimen.pop_yoff));
        mPopupWindow.setAnimationStyle(R.style.popup_window_menu);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }

    public void addItems(List<SpinnerItem> popupList)
    {
        mPopupList.clear();
        mPopupList.addAll(popupList);
    }

    public void addItem(SpinnerItem popup)
    {
        mPopupList.add(popup);
    }

    public void dismiss()
    {
        mPopupWindow.dismiss();
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListView.setOnItemClickListener(listener);
    }

    class PopupListAdapter extends BaseAdapter {

        @Override
        public int getCount()
        {
            return mPopupList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mPopupList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.popup_menu_widget_list_item, null);
                holder.mImgView = (ImageView)convertView.findViewById(R.id.popup_img);
                holder.mTextView = (TextView)convertView.findViewById(R.id.popup_txt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            SpinnerItem popup = mPopupList.get(position);
            if (popup.isHideImg()) {
                holder.mImgView.setVisibility(View.GONE);
            } else {
                holder.mImgView.setVisibility(View.VISIBLE);
                holder.mImgView.setImageResource(popup.getResId());
            }
            holder.mTextView.setText(popup.getText());
            return convertView;
        }

        class ViewHolder {
            public ImageView mImgView;
            public TextView mTextView;
        }
    }

    public void changeItemImage(int position, int resourceId)
    {
        if (position < 0 || position >= mPopupList.size()) { return; }
        mPopupList.get(position).setResId(resourceId);
    }
}
