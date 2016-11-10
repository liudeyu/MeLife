package com.gzmelife.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.DeviceNameAndIPBean;

public class DeviceCenterAdapter extends BaseAdapter {
	Context context;

	private List<DeviceNameAndIPBean> list;

	public DeviceCenterAdapter(Context context, List<DeviceNameAndIPBean> list) {
		this.context = context;
		this.list = list;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_lv_pms_device, null);
			viewHolder.tv_pmsName = (TextView) convertView
					.findViewById(R.id.tv_pmsName);
			viewHolder.tv_wifiName = (TextView) convertView
					.findViewById(R.id.tv_wifiName);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		DeviceNameAndIPBean bean = list.get(position);
		viewHolder.tv_pmsName.setText(bean.getName());
		viewHolder.tv_wifiName.setText("WIFI: " + bean.getWifiName()/* + "\nPMS的IP: " + bean.getIp()*/);
		
		return convertView;
	}

	class ViewHolder {
		TextView tv_pmsName;
		TextView tv_wifiName;
	}
}
