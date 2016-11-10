package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.GoodFoodSearchActivity;
import com.gzmelife.app.bean.SearchAutoData;
import com.gzmelife.app.tools.MyLogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchsAutoAdapter extends BaseAdapter {

	MyLogger HHDLog = MyLogger.HHDLog();

	private Context mContext;
	private ArrayList<SearchAutoData> mOriginalValues;// 所有的Item
	public List<SearchAutoData> mObjects;// 过滤后的item
	private final Object mLock = new Object();
	private int mMaxMatch = 10;// 最多显示多少个选项,负数表示全部
	private OnClickListener mOnClickListener;

	public SearchsAutoAdapter(Context context, int maxMatch,
			OnClickListener onClickListener) {
		this.mContext = context;
		this.mMaxMatch = maxMatch;
		this.mOnClickListener = onClickListener;
		mObjects = mOriginalValues;
	}

	public SearchsAutoAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		HHDLog.v("搜索结果的Adapter");
		//Log.i("cyl", "getCount");
		return null == mObjects ? 0 : mObjects.size();
	}

	@Override
	public Object getItem(int position) {
		return null == mObjects ? 0 : mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		AutoHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.auto_seach_list_item, parent, false);
			holder = new AutoHolder();
			holder.content = (TextView) convertView
					.findViewById(R.id.auto_content);
			holder.auto_linear = (RelativeLayout) convertView
					.findViewById(R.id.auto_linear);
			convertView.setTag(holder);
		} else {
			holder = (AutoHolder) convertView.getTag();
		}
		SearchAutoData data = mObjects.get(position);
		holder.content.setText(data.getName());

		// holder.auto_linear.setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// mObjects.remove(position);
		// // clearData();
		// notifyDataSetChanged();
		// return false;
		// }
		// });

		// holder.addButton.setTag(data);
		// holder.addButton.setOnClickListener(mOnClickListener);
		return convertView;
	}

	private class AutoHolder {
		TextView content;
		TextView addButton;
		RelativeLayout auto_linear;
	}
}
