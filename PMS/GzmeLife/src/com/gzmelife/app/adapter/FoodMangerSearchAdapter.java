package com.gzmelife.app.adapter;


import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.bean.SerachFoodBean;
import com.gzmelife.app.tools.ImgLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodMangerSearchAdapter extends BaseAdapter{
	ViewHolder viewHolder ;
	Context context;
	private List<SerachFoodBean> serachFoodBeanList;
	public FoodMangerSearchAdapter(Context context,List<SerachFoodBean> serachFoodBeanList) {
		this.context=context;
		this.serachFoodBeanList=serachFoodBeanList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return serachFoodBeanList.size();
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
		
		if(convertView==null){
			
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_serach_foodcook, null);
			viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(serachFoodBeanList.get(position).getName());
		
		return convertView;
	}
	static class ViewHolder{
		TextView tv_name;
		
	}
		
}
