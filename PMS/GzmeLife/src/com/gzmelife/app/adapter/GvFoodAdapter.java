package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GvFoodAdapter extends BaseAdapter {
	List<LocalFoodMaterialLevelThree> foods;
	Context context;
	ViewHolder viewHolder;

	public GvFoodAdapter(List<LocalFoodMaterialLevelThree> foods,
			Context context) {
		super();
		this.foods = foods;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return foods.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_food_name, null);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(foods.get(position).getName());
		return convertView;
	}
	class ViewHolder {
		TextView tv_name;
	}

}
