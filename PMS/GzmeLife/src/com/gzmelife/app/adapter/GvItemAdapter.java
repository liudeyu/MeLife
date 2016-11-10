package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.SecondClassifyActivity;
import com.gzmelife.app.bean.FindTowMenuCategoryFirstBean;
import com.gzmelife.app.bean.FindTowMenuCategorySecondBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class GvItemAdapter extends BaseAdapter {
/*	List<LocalFoodMaterialLevelThree> foods;*/
	Context context;
	ViewHolder viewHolder;
	private boolean isClickable;
	private List<FindTowMenuCategorySecondBean> categorySecondBeanList ;

	public GvItemAdapter( Context context,List<FindTowMenuCategorySecondBean> categorySecondBeanList) {
		super();
	/*	this.foods = foods;*/
		this.context = context;
		this.categorySecondBeanList=categorySecondBeanList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categorySecondBeanList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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
		viewHolder.tv_name.setText(categorySecondBeanList.get(position).getMscName());
		
//		viewHolder.tv_name.setChecked(categorySecondBeanList.get(position).isChecked());
//        viewHolder.tv_name.setEnabled(isClickable);
//        viewHolder.tv_name.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				list.get(position).setChecked(!list.get(position).isChecked());
//				viewHolder.cb_taste.setChecked(list.get(position).isChecked());
//				if (onReceiver != null) {
//					if (flag == 0) {
//						onReceiver.onCheckChange(bean.getId() + "", list.get(position).isChecked());
//					} else if (flag == 1) {
//						onReceiver.onCheckChange(bean.getName(), list.get(position).isChecked());
//					}
//				}
//			}
//		});
		
		
		viewHolder.tv_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				viewHolder.tv_name
				Intent intent = new Intent(context,
						SecondClassifyActivity.class);
				intent.putExtra("name", categorySecondBeanList.get(position).getMscName());
				intent.putExtra("categoryId", categorySecondBeanList.get(position).getMscId()+"");
				
				context.startActivity(intent);
			}
		});
		
		
		
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
	}

}
