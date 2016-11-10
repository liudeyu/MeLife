package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.MyDeviceBean;
import com.gzmelife.app.tools.MyLog;

@SuppressLint("InflateParams")
public class MyDeviceAdapter extends BaseAdapter {

	private List<MyDeviceBean> list;
	
	private LayoutInflater inflater;
//	private Context context;
	
	private OnReceiver onReceiver;
	
	public MyDeviceAdapter(Context context, List<MyDeviceBean> list, OnReceiver onReceiver) {
//		this.context = context;
		this.list = list;
		this.onReceiver = onReceiver;
		inflater = LayoutInflater.from(context);
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
		MyLog.d(MyLog.TAG_D, "--->position=" + position);
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lv_my_device, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MyDeviceBean bean = list.get(position);
		holder.tv_name.setText(bean.getNumber());
		
		holder.iv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onReceiver != null) {
					onReceiver.onClick(position);
				}
			}
		});
		
		return convertView;
	}
	
	public interface OnReceiver {
		void onClick(int position);
	}
	
	class ViewHolder {
		TextView tv_name;
		ImageView iv_delete;
	}

}
