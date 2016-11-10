package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.FindCollectionMenuBookBean;
import com.gzmelife.app.tools.ImgLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FindCollectionMenuBookAdapter extends BaseAdapter {
	private String TAG="FindCollectionMenuBookAdapter";
	ViewHolder viewHolder;
	Context context;
	List<FindCollectionMenuBookBean> cookBookBeans;

	public FindCollectionMenuBookAdapter(Context context) {
		this.context = context;
		cookBookBeans = new ArrayList<FindCollectionMenuBookBean>();
	}

	public void setData(List<FindCollectionMenuBookBean> cookBookBeans) {
		this.cookBookBeans = cookBookBeans;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cookBookBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_lv_foodcook, null);

			viewHolder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(cookBookBeans.get(position).getName());
		viewHolder.tv_time.setText(cookBookBeans.get(position).getCreateTime());
//		new ImgLoader(context).showPic(cookBookBeans.get(position).getLogoPath(), viewHolder.iv_icon);
		new ImgLoader(context).showPic(cookBookBeans.get(position).getLogoPath(), viewHolder.iv_icon,R.drawable.icon_pms_file);
		Log.i(TAG, cookBookBeans.get(position).getLogoPath());
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_time;
	}

}
