package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.tools.ImgLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LvfoodSearchAdapter extends BaseAdapter {
	ViewHolder viewHolder;
	Context context;
	private List<SearchMenuBookBean> searchMenuBookBeanList = new ArrayList<SearchMenuBookBean>();

	public LvfoodSearchAdapter(Context context,
			List<SearchMenuBookBean> searchMenuBookBeanList) {
		this.context = context;
		this.searchMenuBookBeanList = searchMenuBookBeanList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchMenuBookBeanList.size();
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
		viewHolder.tv_name.setText(searchMenuBookBeanList.get(position)
				.getName());
		viewHolder.tv_time.setText(searchMenuBookBeanList.get(position)
				.getCreateTime());
		if (!searchMenuBookBeanList.get(position).getLogoPath().equals("")) {
			new ImgLoader(context).showPic(searchMenuBookBeanList.get(position)
					.getLogoPath(), viewHolder.iv_icon);
		}else{
			viewHolder.iv_icon.setImageResource(R.drawable.icon_pms_file);
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_time;

	}

}
