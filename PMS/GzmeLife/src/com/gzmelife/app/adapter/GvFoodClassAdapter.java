package com.gzmelife.app.adapter;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.SecondaryFoodClassificationActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GvFoodClassAdapter extends BaseAdapter{
	Context context;
	ViewHolder viewHolder;
	public GvFoodClassAdapter(Context context) {
		this.context=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView =	LayoutInflater.from(context).inflate(R.layout.item_gv_food,  null);
			viewHolder.tv=(TextView) convertView.findViewById(R.id.tv);
			viewHolder.iv=(ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		viewHolder.iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,SecondaryFoodClassificationActivity.class);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		TextView tv;
		ImageView iv;
		
	}
	

}