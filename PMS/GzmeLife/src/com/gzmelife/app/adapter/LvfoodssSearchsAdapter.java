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

public class LvfoodssSearchsAdapter extends BaseAdapter {
	ViewHolder viewHolder;
	Context context;
	private ArrayList<String> thressList = new ArrayList<String>();

	public LvfoodssSearchsAdapter(Context context, ArrayList<String> thressList) {
		this.context = context;
		this.thressList = thressList;
		System.out.println(">>>>>thressList+++++" + thressList);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return thressList.size();
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
		for (int i = 0; i < thressList.size(); i++) {
			String name = thressList.get(i);
			viewHolder.tv_name.setText(name);
			System.out.println(">>>>thressList.get(position)=====" + name);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_add;

	}

}
