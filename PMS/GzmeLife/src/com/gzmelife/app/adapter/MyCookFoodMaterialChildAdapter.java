package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;

/**
 */
@SuppressLint("InflateParams")
public class MyCookFoodMaterialChildAdapter extends BaseAdapter {

	private String TAG="MyCookFoodMaterialChildAdapter";
	private Context context;

	private List<LocalFoodMaterialLevelThree> list;

	private boolean isClickable;

	private OnReceiver onReceiver;

	private int flag;

	private LayoutInflater inflater;

	/** flag 0:返回选择的id，1：返回选择的名字 */
	public MyCookFoodMaterialChildAdapter(Context context,
			List<LocalFoodMaterialLevelThree> list, int flag,
			OnReceiver onReceiver) {
		super();
		this.context = context;
		this.list = list;
		this.flag = flag;
		this.onReceiver = onReceiver;

		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
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
		final ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.item_lv_my_foodmaterial_child, null);
			viewHolder.cb_taste = (CheckBox) convertView
					.findViewById(R.id.cb_taste);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final LocalFoodMaterialLevelThree bean = list.get(position);
		Log.i(TAG, "修改步骤UID  name:"+list.get(position).getName()+"--：id："+list.get(position).getId());

		viewHolder.cb_taste.setText(bean.getName());
		viewHolder.cb_taste.setChecked(list.get(position).isChecked());
		viewHolder.cb_taste.setEnabled(true);
		viewHolder.cb_taste.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setChecked(!list.get(position).isChecked());
				viewHolder.cb_taste.setChecked(list.get(position).isChecked());
				if (onReceiver != null) {
					if (flag == 0) {
						onReceiver.onCheckChange(bean.getId() + "",bean.getId() + "",
								list.get(position).isChecked());
					} else if (flag == 1) {
						onReceiver.onCheckChange(bean.getName(),bean.getId() + "",
								list.get(position).isChecked());
					}
				}
				System.out.println(">>>>>>>====>>>>");
			}
		});

		return convertView;
	}

	public interface OnReceiver {
		void onCheckChange(String name,String id,  boolean isChecked);
	}

	public static class ViewHolder {
		CheckBox cb_taste;
	}

	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

}
