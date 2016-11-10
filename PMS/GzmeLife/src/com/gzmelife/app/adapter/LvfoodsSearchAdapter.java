package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.bean.SearchFoodBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LvfoodsSearchAdapter extends BaseAdapter {
	ViewHolder viewHolder;
	Context context;
	private List<SearchFoodBean> searchMenuBookBeanList = new ArrayList<SearchFoodBean>();

	public LvfoodsSearchAdapter(Context context,
			List<SearchFoodBean> searchMenuBookBeanList) {
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
					R.layout.item_lv_foodscook, null);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_add = (TextView) convertView
					.findViewById(R.id.tv_add);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(searchMenuBookBeanList.get(position)
				.getName());

		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_add;

	}

}
