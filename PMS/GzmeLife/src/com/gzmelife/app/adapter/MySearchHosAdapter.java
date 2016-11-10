package com.gzmelife.app.adapter;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.SearchHosBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MySearchHosAdapter extends BaseAdapter{
	private List<SearchHosBean> list;
	private Context context;
	
	
	
	public MySearchHosAdapter(List<SearchHosBean> list, Context context) {
		this.list = list;
		this.context = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.auto_seach_list_item, null);
		TextView tv = (TextView) convertView.findViewById(R.id.auto_content);
		tv.setText(list.get(position).getContent());
		return convertView;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
}
