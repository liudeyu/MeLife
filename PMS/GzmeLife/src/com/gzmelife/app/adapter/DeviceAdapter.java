package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.tools.MyLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter{
	Context context;
	ViewHolder viewHolder;
	List<String> codes;	
	String TAG="DeviceAdapter";
	public DeviceAdapter(Context context,List<String> codes) {
		this.context=context;
		this.codes=codes;
	}
	public DeviceAdapter(Context context) {
		this.context=context;
	}
	public int getCount() {
		return codes.size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void addItem(String code) {
		codes.add(code);
		notifyDataSetChanged();
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_fillininfo, null);
			viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.iv_delete= (ImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		MyLog.i(TAG, "==========================>>>>>>>====>==========" );
		viewHolder.tv_name.setText(codes.get(position));
		
		
		viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				codes.remove(position);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_name;
		ImageView iv_delete;
	}
}
